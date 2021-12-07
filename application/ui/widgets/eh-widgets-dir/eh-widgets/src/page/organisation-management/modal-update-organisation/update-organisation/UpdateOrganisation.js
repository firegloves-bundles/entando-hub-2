import { Content, TextInput } from "carbon-components-react"
import { useState } from "react";
import { organisationSchema } from "../../../../helpers/validation/organisationSchema"
import { CHAR_LENGTH, DESCRIPTION_MAX_LENGTH, LEAST_CHAR_NAME_MSG, MAX_CHAR_LENGTH, MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM, MAX_CHAR_NAME_MSG, NAME_REQ_MSG } from "../../../../helpers/constants"
/*

Organisation:
{
    "name": "Entando inc.",
    "description": "Entando inc.",
    "bundleGroups": [],
    "organisationId": "1"
}

*/

const UpdateOrganisation = ({
  organisationObj,
  onDataChange,
  validationResult,
}) => {
  const [orgNameLength, setOrgNameLength] = useState(false);
  const [orgDescLength, setOrgDescLength] = useState(false);
  const [isChanged, setIsChanged] = useState(false);

  const changeOrganisation = (field, value) => {
    const newObj = {
      ...organisationObj,
    }
    newObj[field] = value
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {

    if (fieldName === 'description') {
      const msg = e.target.value.length > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM ? DESCRIPTION_MAX_LENGTH: ""
      validationResult["description"] = [msg]
    }

    if (fieldName === 'name') {
      !isChanged && setIsChanged(true)
      if (isChanged && e.target.value.length < CHAR_LENGTH) {
        const errorMessageForLengthZeroOrThree = e.target.value.length === 0 ? NAME_REQ_MSG : LEAST_CHAR_NAME_MSG
        validationResult["name"] = [errorMessageForLengthZeroOrThree]
      }
      if (isChanged && e.target.value.length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [MAX_CHAR_NAME_MSG]
      }
    }

    fieldName === 'description' && setOrgDescLength(e.target.value.length)
    fieldName === 'name' && setOrgNameLength(e.target.value.length)
    changeOrganisation(fieldName, e.target.value)
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={
            (isChanged && (orgNameLength < CHAR_LENGTH || orgNameLength > MAX_CHAR_LENGTH)) && !!validationResult["name"]
          }
          invalidText={
            (isChanged && (orgNameLength < CHAR_LENGTH || orgNameLength > MAX_CHAR_LENGTH)) ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          value={organisationObj.name}
          labelText={`Name ${organisationSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
        />
        <TextInput
          invalid={orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && !!validationResult["description"]}
          invalidText={
            orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && (
            validationResult["description"] &&
            validationResult["description"].join("; "))
          }
          id="description"
          value={organisationObj.description}
          labelText={`Description ${organisationSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}

export default UpdateOrganisation
