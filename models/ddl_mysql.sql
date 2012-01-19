
    create table article (
        articleID bigint not null auto_increment,
        lastModified datetime not null,
        created datetime not null,
        doi varchar(255) not null unique,
        title longtext,
        eIssn varchar(255),
        state integer,
        archiveName varchar(255),
        description longtext,
        rights longtext,
        language varchar(255),
        format varchar(255),
        pages varchar(255),
        eLocationId varchar(255),
        url varchar(255),
        date datetime,
        volume varchar(255),
        issue varchar(255),
        journal varchar(255),
        publisherLocation varchar(255),
        publisherName varchar(255),
        primary key (articleID)
    );

    create table articleAsset (
        articleAssetID bigint not null auto_increment,
        lastModified datetime not null,
        created datetime not null,
        doi varchar(255) not null,
        extension varchar(255) not null,
        contextElement varchar(255),
        contentType varchar(255),
        title varchar(255),
        description varchar(255),
        size bigint,
        articleID bigint,
        sortOrder integer,
        primary key (articleAssetID),
        unique (doi, extension)
    );

    create table articleCategoryJoinTable (
        articleID bigint not null,
        categoryID bigint not null,
        primary key (articleID, categoryID)
    );

    create table articleCollaborativeAuthors (
        articleID bigint not null,
        name varchar(255),
        sortOrder integer not null,
        primary key (articleID, sortOrder)
    );

    create table articlePerson (
        articlePersonID bigint not null auto_increment,
        type varchar(255) not null,
        lastModified datetime not null,
        created datetime not null,
        fullName varchar(255),
        givenNames varchar(255),
        surnames varchar(255),
        suffix varchar(255),
        articleID bigint,
        sortOrder integer,
        primary key (articlePersonID)
    );

    create table articleRelationship (
        articleRelationshipID bigint not null auto_increment,
        lastModified datetime not null,
        created datetime not null,
        parentArticleID bigint not null,
        otherArticleDoi varchar(255),
        otherArticleID bigint,
        type varchar(255),
        sortOrder integer,
        primary key (articleRelationshipID)
    );

    create table articleType (
        articleID bigint not null,
        type varchar(255)
    );

    create table category (
        categoryID bigint not null auto_increment,
        lastModified datetime not null,
        created datetime not null,
        mainCategory varchar(100),
        subCategory varchar(100),
        primary key (categoryID),
        unique (mainCategory, subCategory)
    );

    create table citedArticle (
        citedArticleID bigint not null auto_increment,
        lastModified datetime not null,
        created datetime not null,
        keyColumn varchar(255),
        year integer,
        displayYear varchar(255),
        month varchar(255),
        day varchar(255),
        volumeNumber integer,
        volume varchar(255),
        issue varchar(255),
        title longtext,
        publisherLocation varchar(255),
        publisherName varchar(255),
        pages varchar(255),
        eLocationID varchar(255),
        journal varchar(255),
        note longtext,
        url varchar(255),
        doi varchar(255),
        citationType varchar(255),
        summary longtext,
        articleID bigint,
        sortOrder integer,
        primary key (citedArticleID)
    );

    create table citedArticleCollaborativeAuthors (
        citedArticleID bigint not null,
        name varchar(255),
        sortOrder integer not null,
        primary key (citedArticleID, sortOrder)
    );

    create table citedPerson (
        citedPersonID bigint not null auto_increment,
        type varchar(255) not null,
        lastModified datetime not null,
        created datetime not null,
        fullName varchar(255),
        givenNames varchar(255),
        surnames varchar(255),
        suffix varchar(255),
        citedArticleID bigint,
        sortOrder integer,
        primary key (citedPersonID)
    );

    create table syndication (
        syndicationID bigint not null auto_increment,
        lastModified datetime not null,
        created datetime not null,
        doi varchar(255) not null,
        target varchar(50) not null,
        status varchar(50),
        submissionCount integer,
        errorMessage longtext,
        lastSubmitTimestamp datetime,
        primary key (syndicationID),
        unique (doi, target)
    );

    create table version (
        versionID bigint not null auto_increment,
        lastModified datetime not null,
        created datetime not null,
        version integer,
        name varchar(255),
        updateInProcess bit,
        primary key (versionID)
    );

    alter table articleAsset 
        add index FKE6D8D79ADFD5CDF3 (articleID), 
        add constraint FKE6D8D79ADFD5CDF3 
        foreign key (articleID) 
        references article (articleID);

    alter table articleCategoryJoinTable 
        add index FK5E567710AA9840AB (categoryID), 
        add constraint FK5E567710AA9840AB 
        foreign key (categoryID) 
        references category (categoryID);

    alter table articleCategoryJoinTable 
        add index FK5E567710DFD5CDF3 (articleID), 
        add constraint FK5E567710DFD5CDF3 
        foreign key (articleID) 
        references article (articleID);

    alter table articleCollaborativeAuthors 
        add index FK204FE589DFD5CDF3 (articleID), 
        add constraint FK204FE589DFD5CDF3 
        foreign key (articleID) 
        references article (articleID);

    alter table articlePerson 
        add index FKD1543EBDFD5CDF3 (articleID), 
        add constraint FKD1543EBDFD5CDF3 
        foreign key (articleID) 
        references article (articleID);

    alter table articleRelationship 
        add index FKA59ED64EAFD8E489 (parentArticleID), 
        add constraint FKA59ED64EAFD8E489 
        foreign key (parentArticleID) 
        references article (articleID);

    alter table articleType 
        add index FK30C56550DFD5CDF3 (articleID), 
        add constraint FK30C56550DFD5CDF3 
        foreign key (articleID) 
        references article (articleID);

    alter table citedArticle 
        add index FK74E3E49DFD5CDF3 (articleID), 
        add constraint FK74E3E49DFD5CDF3 
        foreign key (articleID) 
        references article (articleID);

    alter table citedArticleCollaborativeAuthors 
        add index FKE0BA6F5C8F1352A1 (citedArticleID), 
        add constraint FKE0BA6F5C8F1352A1 
        foreign key (citedArticleID) 
        references citedArticle (citedArticleID);

    alter table citedPerson 
        add index FK29A133A28F1352A1 (citedArticleID), 
        add constraint FK29A133A28F1352A1 
        foreign key (citedArticleID) 
        references citedArticle (citedArticleID);
