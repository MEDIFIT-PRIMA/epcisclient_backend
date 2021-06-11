# GenericModelGeneralInformation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | [**kotlin.String**](.md) | A name given to the model or data | 
**source** | [**kotlin.String**](.md) | A source from which the model/data is derived |  [optional]
**identifier** | [**kotlin.String**](.md) | An unambiguous ID given to the model or data. This can also be created automatically by a software tool | 
**author** | [**kotlin.Array&lt;Contact&gt;**](Contact.md) | Person(s) who generated the model code or generated the data set originally |  [optional]
**creator** | [**kotlin.Array&lt;Contact&gt;**](Contact.md) | The person(s) that created this FSK file including all metadata |  [optional]
**creationDate** | [**java.time.LocalDate**](java.time.LocalDate.md) | Creation date/time of the FSK file | 
**modificationDate** | [**kotlin.Array&lt;java.time.LocalDate&gt;**](java.time.LocalDate.md) | Date/time of the last version of the FSK file |  [optional]
**rights** | [**kotlin.String**](.md) | Rights granted for usage, distribution and modification of this FSK file | 
**availability** | [**kotlin.String**](.md) | Availability of data or model, i.e. if the annotated model code / data is included in this FSK file |  [optional]
**url** | [**kotlin.String**](.md) | Web address referencing the resource location (data for example) |  [optional]
**format** | [**kotlin.String**](.md) | File extension of the model or data file (including version number of format if applicable) |  [optional]
**reference** | [**kotlin.Array&lt;Reference&gt;**](Reference.md) |  | 
**language** | [**kotlin.String**](.md) | A language of the resource (some data or reports can be available in French language for example) |  [optional]
**software** | [**kotlin.String**](.md) | The program or software language in which the model has been implemented |  [optional]
**languageWrittenIn** | [**kotlin.String**](.md) | Software language used to write the model, e.g. R or MatLab |  [optional]
**modelCategory** | [**ModelCategory**](ModelCategory.md) |  |  [optional]
**status** | [**kotlin.String**](.md) | The curation status of the model |  [optional]
**objective** | [**kotlin.String**](.md) | Objective of the model or data |  [optional]
**description** | [**kotlin.String**](.md) | General description of the study, data or model |  [optional]
