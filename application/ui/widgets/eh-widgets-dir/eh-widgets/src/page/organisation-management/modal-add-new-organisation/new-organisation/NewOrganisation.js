import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { organisationSchema } from "../../../../helpers/validation/organisationSchema"
import { CHAR_LENGTH, MAX_CHAR_LENGTH, NAME_REQ_MSG, LEAST_CHAR_NAME_MSG, MAX_CHAR_NAME_MSG, MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM, DESCRIPTION_MAX_LENGTH } from "../../../../helpers/constants"

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}
 */

const NewOrganisation = ({ onDataChange, validationResult }) => {
  const [organisation, setOrganisation] = useState({
    name: "",
    description: "",
  })
  // const [validationErrorMsg,setValidationErrorMsg] = useState({message: NAME_REQ_MSG});
  // const [isChanged, setIsChanged] = useState(false);
  const [orgNameLength, setOrgNameLength] = useState(false);
  const [orgDescLength, setOrgDescLength] = useState(false);

  const changeOrganisation = (field, value) => {
    const newObj = {
      ...organisation,
    }
    newObj[field] = value
    setOrganisation(newObj)
    onDataChange(newObj)
  }

  /**
   * @param {*} e Event object to get value of field
   * @param {*} fieldName Name of the field
   * @description Validation & Setting on fields.
   */
  const onChangeHandler = (e, fieldName) => {
    if (fieldName === 'description' && e.target.value.trim().length) {
      const msg = e.target.value.trim().length > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM ? DESCRIPTION_MAX_LENGTH : ""
      validationResult["description"] = [msg]
      setOrgDescLength(e.target.value.trim().length)
    }

    if (fieldName === 'name') {
      if (e.target.value.trim().length < CHAR_LENGTH) {
        const msg = e.target.value.trim().length === 0 ? NAME_REQ_MSG : LEAST_CHAR_NAME_MSG
        validationResult["name"] = [msg]
      }
      if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [MAX_CHAR_NAME_MSG]
      }
      setOrgNameLength(e.target.value.trim().length)
    }
    changeOrganisation(fieldName, e.target.value)
  }

  /**
   * @param {*} e Event object to get value of field
   * @param {*} field Name of the field
   * @description Trimming whitespaces from the field value.
   */
  const trimBeforeFormSubmitsHandler = (e, field) => {
    changeOrganisation(field, e.target.value.trim())
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={((orgNameLength < CHAR_LENGTH || orgNameLength > MAX_CHAR_LENGTH)) && !!validationResult["name"]}
          invalidText={
            ((orgNameLength < CHAR_LENGTH || orgNameLength > MAX_CHAR_LENGTH)) ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          value={organisation.name}
          labelText={`Name ${organisationSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
        />
        <TextInput
          invalid={orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && !!validationResult["description"]}
          invalidText={
            orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && (validationResult["description"] && validationResult["description"].join("; "))
          }
          id="description"
          value={organisation.description}
          labelText={`Description ${organisationSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewOrganisation
