import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { organisationSchema } from "../../../../helpers/validation/organisationSchema"
import i18n from "../../../../i18n"

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

  const changeOrganisation = (field, value) => {
    const newObj = {
      ...organisation,
    }
    newObj[field] = value
    setOrganisation(newObj)
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {
    changeOrganisation(fieldName, e.target.value)
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={!!validationResult["name"]}
          invalidText={
            validationResult["name"] && validationResult["name"].join("; ")
          }
          id="name"
          labelText={`${i18n.t('page.management.label.name')} ${organisationSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
        />
        <TextInput
          invalid={!!validationResult["description"]}
          invalidText={
            validationResult["description"] &&
            validationResult["description"].join("; ")
          }
          id="description"
          labelText={`${i18n.t('page.management.label.description')} ${organisationSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewOrganisation
