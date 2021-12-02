import { Content, TextInput } from "carbon-components-react"
import { useState } from "react";
import { organisationSchema } from "../../../../helpers/validation/organisationSchema"
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
  const requiredMinChar = 3;
  const changeOrganisation = (field, value) => {
    const newObj = {
      ...organisationObj,
    }
    newObj[field] = value
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {
    fieldName === 'name' && setOrgNameLength(e.target.value.length)
    changeOrganisation(fieldName, e.target.value)
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={orgNameLength < requiredMinChar && !!validationResult["name"]}
          invalidText={
            orgNameLength < requiredMinChar ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          value={organisationObj.name}
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
          value={organisationObj.description}
          labelText={`Description ${organisationSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}

export default UpdateOrganisation
