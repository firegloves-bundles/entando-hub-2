import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { categorySchema } from "../../../../helpers/validation/categorySchema"
import i18n from "../../../../i18n"
import { CHAR_LENGTH, MAX_CHAR_LENGTH, MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM } from "../../../../helpers/constants"

const NewCategory = ({ onDataChange, validationResult }) => {
  const [category, setCategory] = useState({
    name: "",
    description: "",
  })
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
      const msg = e.target.value.trim().length > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM ? i18n.t('formValidationMsg.description') : ""
      validationResult["description"] = [msg]
      fieldName === 'description' && setCatDescLength(e.target.value.trim().length)
    }

    if (fieldName === 'name') {
      if (e.target.value.trim().length < CHAR_LENGTH) {
        const msg = e.target.value.trim().length === 0 ? i18n.t('formValidationMsg.nameRequired') : i18n.t('formValidationMsg.min3Char')
        validationResult["name"] = [msg]
      }
      if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [i18n.t('formValidationMsg.max25Char')]
      }
      fieldName === 'name' && setCatNameLength(e.target.value.trim().length)
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
          invalid={((catNameLength < CHAR_LENGTH || catNameLength > MAX_CHAR_LENGTH)) && !!validationResult["name"]}
          invalidText={
            ((catNameLength < CHAR_LENGTH || catNameLength > MAX_CHAR_LENGTH)) ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id="name"
          labelText={`${i18n.t('page.management.label.name')} ${categorySchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          value={category.name}
          onChange={(e) => onChangeHandler(e, "name")}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
        />
        <TextInput
          invalid={catDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && !!validationResult["description"]}
          invalidText={
            catDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && ( validationResult["description"] && validationResult["description"].join("; "))
          }
          id="description"
          labelText={`${i18n.t('page.management.label.description')} ${categorySchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
          value={category.description}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewCategory
