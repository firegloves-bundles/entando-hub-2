import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { categorySchema } from "../../../../helpers/validation/categorySchema"
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

const NewCategory = ({ onDataChange, validationResult }) => {
  const [category, setCategory] = useState({
    name: "",
    description: "",
  })

  const changeCategory = (field, value) => {
    const newObj = {
      ...category,
    }
    newObj[field] = value
    setCategory(newObj)
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {
    changeCategory(fieldName, e.target.value)
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
          labelText={`${i18n.t('page.management.label.name')} ${categorySchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
        />
        <TextInput
          invalid={!!validationResult["description"]}
          invalidText={
            validationResult["description"] &&
            validationResult["description"].join("; ")
          }
          id="description"
          labelText={`${i18n.t('page.management.label.description')} ${categorySchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewCategory
