module.exports = function($q, $http, param) {

  var genderOptions = {
    m: {
      value: 'm',
      label: "GENERIC_SALUTATION_MALE"
    },
    f: {
      value: 'f',
      label: "GENERIC_SALUTATION_FEMALE"
    }
  };

  var cachedMetadata = {};

  var transformResponse = function(response) {
    return response.data;
  };
  // due to the way field values are processed on the server side
  // we need to do some extra processing here.
  // FIXME: revise the REST API, make it less... awkward.
  var encodeFieldValues = function(fieldValues, fields) {
    var encoded = {};
    fields.forEach(function(field) {
      value = fieldValues[field.pk];
      switch (field.fk_type) {
        case 1: // text input
          encoded[field.pk] = value;
          break;
        case 2: // single-select
          for (var i in field.optionalFieldTypeContentsFacade) {
            if (field.optionalFieldTypeContentsFacade[i].pk === value) {
              encoded[field.pk] = field.optionalFieldTypeContentsFacade[i].content;
              break;
            }
          }

          break;
        case 3: // multi-select
          encoded[field.pk] = [];

          for (var i in field.optionalFieldTypeContentsFacade) {
            if (value.indexOf(field.optionalFieldTypeContentsFacade[i].pk) !== -1) {
              encoded[field.pk].push(field.optionalFieldTypeContentsFacade[i].content);
            }
          }

          break;
        default:
          throw new Error("unsupported field type: " + field.fk_type);
      }
    });
    return JSON.stringify(encoded);
  };
  var decodeFieldValues = function(fields) {
    var values = {};
    fields.forEach(function(field) {
      switch (field.fk_type) {
        case 1: // text input
          values[field.pk] = field.value
          break;
        case 2: // single-select
          for (var i in field.optionalFieldTypeContentsFacade) {
            if (field.optionalFieldTypeContentsFacade[i].isSelected) {
              values[field.pk] = field.optionalFieldTypeContentsFacade[i].pk;
              break;
            }
          }
          break;
        case 3: // multi-select
          values[field.pk] = [];
          for (var i in field.optionalFieldTypeContentsFacade) {
            if (field.optionalFieldTypeContentsFacade[i].isSelected) {
              values[field.pk].push(field.optionalFieldTypeContentsFacade[i].pk);
            }
          }
          break;
        default:
          throw new Error("unsupported field type: " + field.fk_type);
      }
    });
    return values;
  };
  var fetchList = function(uuid) {
    return $http({
        method: 'GET',
        url: 'api/delegation/' + uuid
      })
      .then(transformResponse);
  };
  var fetchPerson = function(uuid, pk) {
    return $q.all([
        $http.get('api/delegation/load/' + uuid + '/' + pk)
        .then(transformResponse)
        .then(function(data) {
          return {
            firstname: data.firstname_a_e1,
            lastname: data.lastname_a_e1,
            gender: genderOptions[data.sex_a_e1],
            functionDescription: data.function_a_e1,
            personId: data.pk
          };
        }),
        $http.get('api/delegation/load/fields/' + uuid + '/' + pk)
        .then(transformResponse)
        .then(decodeFieldValues),
        $http.get('api/delegation/image/' + uuid + '/' + pk)
        .then(transformResponse)
        .then(function(data) {
          return $http.get('api/fileupload/download/' + data.status);
        })
        .then(transformResponse)
        .then(function(data) {
          return data.status
        }, function(error) {
          console.log("fyi:", error);
          //ignore errors at this point:
          //  it may very well be that no image exists.
          return null;
        })

      ])
      .then(function(results) {
        var person = results[0];
        person.category = results[1];
        person.fields = results[2];
        person.image = results[3];
        return person;
      });
  };
  var fetchMetadata = function(uuid) {
    return $q.all([
        $http.get('api/delegation/fields/list/function/' + uuid)
        .then(transformResponse),
        $http.get('api/delegation/' + uuid + '/data')
        .then(transformResponse)
      ])
      .then(function(results) {
        return cachedMetadata[uuid] = {
          functions: results[0],
          extraFields: results[1],
          genderOptions: genderOptions
        };
      });
  };
  var savePerson = function(uuid, person) {
    var metadata;
    if (cachedMetadata.hasOwnProperty(uuid)) {
      metadata = $q.resolve(cachedMetadata[uuid].extraFields);
    } else {
      metadata = $http
        .get('api/delegation/fields/list/function')
        .then(transformResponse);
    }
    return metadata.then(function(fieldMetadata) {
        return $http({
          method: 'POST',
          url: 'api/delegation/' + uuid + '/register',
          headers: {
            'Content-Type': undefined
          },
          data: param({
            firstname: person.firstname,
            lastname: person.lastname,
            gender: person.gender.value,
            category: person.category,
            fields: encodeFieldValues(person.fields, fieldMetadata),
            functionDescription: person.functionDescription,
            personId: person.personId,
            hasTempImage: !!person.image
          })
        });
      })
      .then(transformResponse)
      .then(function(data) {
        switch (data.status) {
          case 'NO_EVENT_DATA':
            throw new Error('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS');
          case 'WRONG_DELEGATION':
            throw new Error('DELEGATION_MESSAGE_NO_EXTRA_FIELDS');
          case 'OK':
            break;
          default:
            var imageUuid = data.status;
            return $http({
              method: 'POST',
              url: 'api/fileupload/save',
              dataType: 'text',
              headers: {
                "Content-Type": undefined
              },
              data: param({
                file: person.image,
                imgUUID: imageUuid
              })
            });
            break;
        }
      })
      .then(function() {
        return "DELEGATION_MESSAGE_DELEGATION_DATA_SAVED_SUCCESSFUL";
      });
  };
  return {
    fetchList: fetchList,
    fetchPerson: fetchPerson,
    fetchMetadata: fetchMetadata,
    savePerson: savePerson
  };
};
