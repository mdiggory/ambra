/* $HeadURL::                                                                          $
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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;

/**
 * Maintains a document ordered non overlapping list of ranges. See {@link #insert insert} for how
 * the non-overlapping constraint is enforced.
 *
 * @author Pradeep Krishnan
 */
public class SelectionRangeList {
  private ArrayList selectionRanges = new ArrayList();

  /**
   * Returns the size of this list.
   *
   * @return the size
   */
  public int size() {
    return selectionRanges.size();
  }

  /**
   * Gets the SelectionRange at the given index.
   *
   * @param i the index
   *
   * @return the SelectionRange
   */
  public SelectionRange get(int i) {
    return (SelectionRange) selectionRanges.get(i);
  }

  /**
   * Inserts a range in this ordered list; splitting it or an already inserted range as necessary
   * so that none of the ranges in this list overlap. UserData is copied (not cloned) into each
   * fragment. Therefore the constraint (size() == number of inserts) does not hold good when
   * overlapping ranges are inseted.
   *
   * @param selectionRange the range to insert
   */
  public void insert(SelectionRange selectionRange) {
    insertAtOrAfter(0, selectionRange);
  }

  /**
   * Surround all regions in this list with the newly created elements with the given name and with
   * the given id attribute name. Id attrivute values will be auto generated and will start with 1
   * and increase in document order.
   * 
   * <p>
   * Note that for partially selected ranges, each surroundable range will have the same id
   * attribute. ie. the id will alway be 1 + the index of the SelectionRange obtained via {@link
   * #get(int) get}.
   * </p>
   *
   * @param nsUri namespace uri for the element node
   * @param elemQName name to assign to each surrounding element
   * @param idAttrQName the id attribute of each surrounding element
   */
  public void surroundContents(String nsUri, String elemQName, String idAttrQName, String firstId) {
    int               length = size();

    RangePointsList[] ranges = new RangePointsList[length];

    for (int i = 0; i < length; i++)
      ranges[i] = new RangePointsList(get(i).getSurroundableRanges());

    // Now modify the document
    for (int i = length - 1; i >= 0; i--) {
      for (int j = ranges[i].length() - 1; j >= 0; j--) {
        Range   range = ranges[i].get(j).toRange();
        Element rNode =
          range.getStartContainer().getOwnerDocument().createElementNS(nsUri, elemQName);
        rNode.setAttributeNS(nsUri, idAttrQName, "" + (i + 1));
        if ((j == 0) && (firstId != null)) {
          rNode.setAttributeNS(nsUri, firstId, "true");
        }
        range.surroundContents(rNode);
      }
    }
  }

  private void insertAtOrAfter(int i, SelectionRange newSelectionRange) {
    int            length = selectionRanges.size();

    SelectionRange selectionRange = null;

    // scan past selectionRanges that are before the new selectionRange
    while ((i < length)
            && newSelectionRange.isAfter(selectionRange = (SelectionRange) selectionRanges.get(i)))
      i++;

    // if the next selectionRange is clearly after or at end, then insert this before
    if ((i >= length) || selectionRange.isAfter(newSelectionRange)) {
      selectionRanges.add(i, newSelectionRange);

      return;
    }

    // there is an overlap. break that into 'before, 'shared' and 'after'
    // first create a new selectionRange for 'before'
    SelectionRange before = null;

    if (selectionRange.startsBefore(newSelectionRange))
      before = selectionRange.splitBefore(newSelectionRange);
    else if (newSelectionRange.startsBefore(selectionRange))
      before = newSelectionRange.splitBefore(selectionRange);

    if (before != null)
      selectionRanges.add(i++, before);

    // now both 'selectionRange' and 'newSelectionRange' start at the same point
    if (selectionRange.endsAfter(newSelectionRange)) {
      // 'shared' is the 'newSelectionRange'. copy userDatas and insert before 'selectionRange'
      newSelectionRange.addAllUserData(selectionRange.getUserDataList());
      selectionRanges.add(i++, newSelectionRange);

      //  'selectionRange' now starts where 'newSelectionRange' ends (ie. it is the 'after' fragment)
      selectionRange.setAsContinuationOf(newSelectionRange);
    } else {
      // 'shared' is the 'selectionRange'. copy userDatas
      selectionRange.addAllUserData(newSelectionRange.getUserDataList());

      if (newSelectionRange.endsAfter(selectionRange)) {
        //  'newSelectionRange' starts where 'selectionRange' ends
        newSelectionRange.setAsContinuationOf(selectionRange);

        // at this point 'newSelectionRange' is after 'selectionRange' and so repeat the whole process
        insertAtOrAfter(i + 1, newSelectionRange);
      }
    }
  }

  private static class RangePoints {
    private Node start;
    private Node end;
    private int  oStart;
    private int  oEnd;

    public RangePoints(Range range) {
      start    = range.getStartContainer();
      oStart   = range.getStartOffset();
      end      = range.getEndContainer();
      oEnd     = range.getEndOffset();
    }

    public Range toRange() {
      Range range = ((DocumentRange) start.getOwnerDocument()).createRange();
      range.setStart(start, oStart);
      range.setEnd(end, oEnd);

      return range;
    }
  }

  private static class RangePointsList {
    RangePoints[] rps;

    public RangePointsList(Range[] ranges) {
      rps = new RangePoints[ranges.length];

      for (int i = 0; i < ranges.length; i++)
        rps[i] = new RangePoints(ranges[i]);
    }

    public int length() {
      return rps.length;
    }

    public RangePoints get(int i) {
      return rps[i];
    }
  }
}
