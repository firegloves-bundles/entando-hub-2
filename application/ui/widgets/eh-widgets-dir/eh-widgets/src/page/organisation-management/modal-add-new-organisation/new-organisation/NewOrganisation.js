import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { organisationSchema } from "../../../../helpers/validation/organisationSchema"
import { CHAR_LENGTH, NAME_REQ_MSG, LEAST_CHAR_NAME_MSG } from "../../../../helpers/constants"

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
  const [validationErrorMsg,setValidationErrorMsg] = useState({message: NAME_REQ_MSG});


  const changeOrganisation = (field, value) => {
    const newObj = {
      ...organisation,
    }
    newObj[field] = value
    setOrganisation(newObj)
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {
    if (fieldName === "name") {
      if (e.target.value.length === 0) {
        setValidationErrorMsg({
          message: NAME_REQ_MSG
        })
      } else if (e.target.value.length < CHAR_LENGTH) {
        setValidationErrorMsg({
          message: LEAST_CHAR_NAME_MSG
        })
      } else {
        setValidationErrorMsg({
          message: ""
        })
      }
    }
    changeOrganisation(fieldName, e.target.value)
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={!!validationResult["name"] && !!validationErrorMsg.message}
          invalidText={
            validationErrorMsg.message
          }
          id="name"
          labelText={`Name ${organisationSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
        />
        <TextInput
          invalid={!!validationResult["description"]}
          invalidText={
            validationResult["description"] &&
            validationResult["description"].join("; ")
          }
          id="description"
          labelText={`Description ${organisationSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewOrganisation
