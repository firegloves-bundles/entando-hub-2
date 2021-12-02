import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { categorySchema } from "../../../../helpers/validation/categorySchema"

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
  const [categoryNameLength, setCategoryNameLength] = useState(false);

  const changeCategory = (field, value) => {
    const newObj = {
      ...category,
    }
    newObj[field] = value
    setCategory(newObj)
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {
    fieldName === 'name' && setCategoryNameLength(e.target.value.length)
    changeCategory(fieldName, e.target.value)
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={categoryNameLength < 3 && !!validationResult["name"]}
          invalidText={
            categoryNameLength < 3 ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          labelText={`Name ${categorySchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
        />
        <TextInput
          invalid={!!validationResult["description"]}
          invalidText={
            validationResult["description"] &&
            validationResult["description"].join("; ")
          }
          id="description"
          labelText={`Description ${categorySchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewCategory
