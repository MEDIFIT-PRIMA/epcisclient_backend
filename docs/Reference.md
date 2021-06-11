# Reference

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**isReferenceDescription** | [**kotlin.Boolean**](.md) | Indicates whether this specific publication serves as the reference description for the model. There has to be at least one reference where this field is set to &#x27;True&#x27; | 
**publicationType** | [**inline**](#PublicationTypeEnum) | The type of publication, e.g. Report, Journal article, Book, Online database, ... |  [optional]
**date** | [**java.time.LocalDate**](java.time.LocalDate.md) | Temporal information on the publication date |  [optional]
**pmid** | [**kotlin.String**](.md) | The PubMed ID related to this publication |  [optional]
**doi** | [**kotlin.String**](.md) | The DOI related to this publication | 
**authorList** | [**kotlin.String**](.md) | Name and surname of the authors who contributed to this publication |  [optional]
**title** | [**kotlin.String**](.md) | Title of the publication in which the model or the data has been described | 
**&#x60;abstract&#x60;** | [**kotlin.String**](.md) | Abstract of the publication in which the model or the data has been described |  [optional]
**journal** | [**kotlin.String**](.md) | Data on the details of the journal in which the model or the data has been described |  [optional]
**volume** | [**kotlin.String**](.md) | Data on the details of the journal in which the model or the data has been described |  [optional]
**issue** | [**kotlin.String**](.md) | Data on the details of the journal in which the model or the data has been described |  [optional]
**status** | [**kotlin.String**](.md) | The status of this publication, e.g. Published, Submitted, etc. |  [optional]
**website** | [**kotlin.String**](.md) | A link to the publication website (different from DOI) |  [optional]
**comment** | [**kotlin.String**](.md) | Further comments related to the reference description, e.g. which section in there describes the specific model or which figure in there can be reproduced with the visualization script |  [optional]

<a name="PublicationTypeEnum"></a>
## Enum: publicationType
Name | Value
---- | -----
publicationType | ABST, ADVS, AGGR, ANCIENT, ART, BILL, BLOG, BOOK, CASE, CHAP, CHART, CLSWK, COMP, CONF, CPAPER, CTLG, DATA, DBASE, DICT, EBOOK, ECHAP, EDBOOK, EJOUR, ELECT, ENCYC, EQUA, FIGURE, GEN, GOVDOC, GRANT, HEAR, ICOMM, INPR, JOUR, JFULL, LEGAL, MANSCPT, MAP, MGZN, MPCT, MULTI, MUSIC, NEW, PAMP, PAT, PCOMM, RPRT, SER, SLIDE, SOUND, STAND, STAT, THES, UNPB, VIDEO
