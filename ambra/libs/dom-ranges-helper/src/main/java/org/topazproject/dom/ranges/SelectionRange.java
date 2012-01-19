/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.topazproject.dom.ranges;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * A helper class to maintain selected Ranges in a document.
 *
 * @author Pradeep Krishnan
 */
public class SelectionRange {
  private List  userDataList = new ArrayList();
  private Range range;

/**
   * Creates a new SelectionRange object.
   *
   * @param range the selected range
   * @param userData corresponding user data or <code>null</code>
   */
  public SelectionRange(Range range, Object userData) {
    this.range               = range;

    if (userData != null)
      userDataList.add(userData);
  }

  private SelectionRange() {
  }

  /**
   * Creates a clone of this object. Clones the Range and copies all user data.
   *
   * @return the newly created object.
   */
  public SelectionRange cloneRange() {
    SelectionRange dup = new SelectionRange();
    dup.userDataList.addAll(userDataList);
    dup.range = range.cloneRange();

    return dup;
  }

  /**
   * Returns the list of user data items
   *
   * @return the list (not a copy, so modifiable)
   */
  public List getUserDataList() {
    return userDataList;
  }

  /**
   * Adds the contents of the list to user data list.
   *
   * @param list the list to add
   */
  public void addAllUserData(List list) {
    userDataList.addAll(list);
  }

  /**
   * Checks if this SelectionRange is after the other.
   *
   * @param other the other range to compare
   *
   * @return returns <code>true</code> if this range is after
   */
  public boolean isAfter(SelectionRange other) {
    //compare end of other to start of this
    int es = range.compareBoundaryPoints(Range.END_TO_START, other.range);

    // -1 if this point before other, 0 if equal, 1 if this after other
    return (es >= 0); // other is before this
  }

  /**
   * Checks if this SelectionRange is before the other.
   *
   * @param other the other range to compare
   *
   * @return returns <code>true</code> if the other range is after this.
   */
  public boolean isBefore(SelectionRange other) {
    //compare start of other to end of this
    int se = range.compareBoundaryPoints(Range.START_TO_END, other.range);

    // -1 if this point before other, 0 if equal, 1 if this after other
    return (se <= 0); // other is after this
  }

  /**
   * Checks if this range starts before the other
   *
   * @param other the other range to compare
   *
   * @return returns <code>true</code> if this range starts before the other
   */
  public boolean startsBefore(SelectionRange other) {
    //compare start of other to start of this
    int ss = range.compareBoundaryPoints(Range.START_TO_START, other.range);

    // -1 if this point before other, 0 if equal, 1 if this after other
    return (ss < 0); // if this starts before other
  }

  /**
   * Checks if this range ends after the other
   *
   * @param other the other range to compare
   *
   * @return returns <code>true</code> if this range ends before the other
   */
  public boolean endsAfter(SelectionRange other) {
    //compare end of other to end of this
    int ee = range.compareBoundaryPoints(Range.END_TO_END, other.range);

    // -1 if this point before other, 0 if equal, 1 if this after other
    return (ee > 0); // if this ends after other
  }

  /**
   * Splits this range into two and sets this range as starting after the split.
   *
   * @param splitPoint the range whose start is the split point
   *
   * @return returns the range before the split point
   */
  public SelectionRange splitBefore(SelectionRange splitPoint) {
    SelectionRange before = cloneRange();
    before.range.setEnd(splitPoint.range.getStartContainer(), splitPoint.range.getStartOffset());
    range.setStart(splitPoint.range.getStartContainer(), splitPoint.range.getStartOffset());

    return before;
  }

  /**
   * Move the start of this range to the end of the other so that this will become a
   * continutation of the other node.
   *
   * @param other the other node
   */
  public void setAsContinuationOf(SelectionRange other) {
    range.setStart(other.range.getEndContainer(), other.range.getEndOffset());
  }

  /**
   * Gets the list of largest non-overlapping sub-ranges each of which can be surrounded by a
   * parent element. Handles partially selected nodes unlike the DOM Ranges {@link
   * org.w3c.dom.ranges.Range#surroundContents surroundContents} method. The ranges returned by
   * this call are guaranteed not to contain partially selected ranges and therefore it is safe to
   * call <code>surroundContents</code> on the returned ranges.
   *
   * @return returns an array of sub-ranges. (will at least have 1 element)
   */
  public Range[] getSurroundableRanges() {
    Node     cac      = range.getCommonAncestorContainer();
    Document document = cac.getOwnerDocument();

    // Select all top level nodes and descendants inside the range.
    NodeIterator it   =
      ((DocumentTraversal) document).createNodeIterator(cac, NodeFilter.SHOW_ALL,
                                                        new RangeNodeFilter(range, false), false);

    Node         n;
    ArrayList    list = new ArrayList();
    Node         prev = null;
    Range        last = null;

    while ((n = it.nextNode()) != null) {
      Range   r     = ((DocumentRange) document).createRange();
      boolean start = (n == range.getStartContainer());
      boolean end   = (n == range.getEndContainer());
      short   type  = n.getNodeType();

      if (type == Node.TEXT_NODE) {
        r.selectNode(n);

        if (start)
          r.setStart(n, range.getStartOffset());

        if (end)
          r.setEnd(n, range.getEndOffset());

        /*
         * Optimization rule
         *
         * Rule 1: text nodes of same parent can be appended to previous range
         */
        if ((type == Node.TEXT_NODE) && (prev != null) && (prev.getNodeType() == Node.TEXT_NODE) &&
            (n.getParentNode() == prev.getParentNode())) {
          // Note: we may have appended complete non-text nodes before; but that is fine
          last.setEnd(r.getEndContainer(), r.getEndOffset());

          continue;
        }

        list.add(last = r);
        prev = n;
      }
    }

    return (Range[]) list.toArray(new Range[0]);
  }

  /*
   * @see java.lang.Object#toString
   */
  public String toString() {
    return range.toString();
  }
}
