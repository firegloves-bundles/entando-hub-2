import { Content, TextInput } from "carbon-components-react"
import { useState } from "react";
import { categorySchema } from "../../../../helpers/validation/categorySchema"
import { CHAR_LENGTH } from "../../../../helpers/constants"
/*

Organisation:
{
    "name": "Entando inc.",
    "description": "Entando inc.",
    "bundleGroups": [],
    "organisationId": "1"
}

*/

const UpdateCategory = ({ categoryObj, onDataChange, validationResult }) => {
  const [categoryNameLength, setCategoryNameLength] = useState(false);

  const changeCategory = (field, value) => {
    const newObj = {
      ...categoryObj,
    }
    newObj[field] = value
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
          invalid={categoryNameLength< CHAR_LENGTH && !!validationResult["name"]}
          invalidText={
            categoryNameLength< CHAR_LENGTH ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          value={categoryObj.name}
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
          value={categoryObj.description}
          labelText={`Description ${categorySchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}

export default UpdateCategory
