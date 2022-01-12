import { Content, TextInput } from "carbon-components-react"
import { useState } from "react";
import { categorySchema } from "../../../../helpers/validation/categorySchema"
import i18n from "../../../../i18n"

import { CHAR_LENGTH, MAX_CHAR_LENGTH, MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM } from "../../../../helpers/constants"

const UpdateCategory = ({ categoryObj, onDataChange, validationResult }) => {
  const [categoryNameLength, setCategoryNameLength] = useState(false);
  const [catDescLength, setCatDescLength] = useState(false);

  const changeCategory = (field, value) => {
    const newObj = {
      ...categoryObj,
    }
    newObj[field] = value
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {
    if (fieldName === 'description') {
      const msg = e.target.value.trim().length > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM ? i18n.t('formValidationMsg.description') : ""
      validationResult["description"] = [msg]
      setCatDescLength(e.target.value.trim().length)
    }

    if (fieldName === 'name') {
      if (e.target.value.trim().length < CHAR_LENGTH) {
        const errorMessageForLengthZeroOrThree = e.target.value.trim().length === 0 ? i18n.t('formValidationMsg.nameRequired') : i18n.t('formValidationMsg.min3Char')
        validationResult["name"] = [errorMessageForLengthZeroOrThree]
      }
      if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [i18n.t('formValidationMsg.max25Char')]
      }
      setCategoryNameLength(e.target.value.trim().length)
    }
    changeCategory(fieldName, e.target.value)
  }

  /**
   * @param {*} e Event object to get value of field
   * @param {*} field Name of the field
   * @description Trimming whitespaces from the field value.
   */
  const trimBeforeFormSubmitsHandler = (e, field) => {
    changeCategory(field, e.target.value.trim())
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={
            (categoryNameLength < CHAR_LENGTH || categoryNameLength > MAX_CHAR_LENGTH) && !!validationResult["name"]
          }
          invalidText={
            (categoryNameLength < CHAR_LENGTH || categoryNameLength > MAX_CHAR_LENGTH) ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          value={categoryObj.name}
          labelText={`${i18n.t('component.bundleModalFields.name')} ${categorySchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
          maxLength={MAX_CHAR_LENGTH}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
        />
        <TextInput
          invalid={catDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && !!validationResult["description"]}
          invalidText={
            catDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && (
            validationResult["description"] &&
            validationResult["description"].join("; "))
          }
          id="description"
          value={categoryObj.description}
          labelText={`${i18n.t('component.bundleModalFields.description')} ${categorySchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
          maxLength={MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
        />
      </Content>
    </>
  )
}

export default UpdateCategory
