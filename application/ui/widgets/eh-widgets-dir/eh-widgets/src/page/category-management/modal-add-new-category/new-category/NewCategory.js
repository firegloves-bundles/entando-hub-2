import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { categorySchema } from "../../../../helpers/validation/categorySchema"
import { CHAR_LENGTH, DESCRIPTION_MAX_LENGTH, LEAST_CHAR_NAME_MSG, MAX_CHAR_LENGTH, MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM, MAX_CHAR_NAME_MSG, NAME_REQ_MSG } from "../../../../helpers/constants"

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
  // const [validationErrorMsg,setValidationErrorMsg] = useState({message: NAME_REQ_MSG});
  const [catNameLength, setCatNameLength] = useState(false);
  const [catDescLength, setCatDescLength] = useState(false);

  const changeCategory = (field, value) => {
    const newObj = {
      ...category,
    }
    newObj[field] = value
    setCategory(newObj)
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {

    if (fieldName === 'description') {
      const msg = e.target.value.length > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM ? DESCRIPTION_MAX_LENGTH: ""
      validationResult["description"] = [msg]
    }

    if (fieldName === 'name') {
      if (e.target.value.length < CHAR_LENGTH) {
        const msg = e.target.value.length === 0 ? NAME_REQ_MSG : LEAST_CHAR_NAME_MSG
        validationResult["name"] = [msg]
      }
      if (e.target.value.length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [MAX_CHAR_NAME_MSG]
      }
    }

    fieldName === 'name' && setCatNameLength(e.target.value.length)
    fieldName === 'description' && setCatDescLength(e.target.value.length)
    changeCategory(fieldName, e.target.value)
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={((catNameLength < CHAR_LENGTH || catNameLength > MAX_CHAR_LENGTH)) && !!validationResult["name"]}
          invalidText={
            ((catNameLength < CHAR_LENGTH || catNameLength > MAX_CHAR_LENGTH)) ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          labelText={`Name ${categorySchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
        />
        <TextInput
          invalid={catDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && !!validationResult["description"]}
          invalidText={
            catDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && ( validationResult["description"] && validationResult["description"].join("; "))
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
