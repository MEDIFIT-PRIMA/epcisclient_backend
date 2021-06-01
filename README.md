# epcisclient_backend
Utility backend for the EPCIS client. It will perform tasks needed for the EPCIS client that are not provided or immediate with the EPCIS backend. It runs at port 8000.

| Route | Description|
| ----- | ---------- |
| /models. |Return `List<JsonElement>` with the *fsk:model* children of the models in the demo server.|
