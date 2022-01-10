import { Content, TextInput } from "carbon-components-react"
import { useState } from "react"
import { CHAR_LENGTH, MAX_CHAR_LENGTH, MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM } from "../../../../helpers/constants"
import { organisationSchema } from "../../../../helpers/validation/organisationSchema"
import i18n from "../../../../i18n"

const NewOrganisation = ({ onDataChange, validationResult }) => {
  const [organisation, setOrganisation] = useState({
    name: "",
    description: "",
  })
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
      const msg = e.target.value.trim().length > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM ? i18n.t('formValidationMsg.description') : ""
      validationResult["description"] = [msg]
      setOrgDescLength(e.target.value.trim().length)
    }

    if (fieldName === 'name') {
      if (e.target.value.trim().length < CHAR_LENGTH) {
        const msg = e.target.value.trim().length === 0 ? i18n.t('formValidationMsg.nameRequired') : i18n.t('formValidationMsg.min3Char')
        validationResult["name"] = [msg]
      }
      if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [i18n.t('formValidationMsg.max25Char')]
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
          labelText={`${i18n.t('page.management.label.name')} ${organisationSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
          maxLength={MAX_CHAR_LENGTH}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
        />
        <TextInput
          invalid={orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && !!validationResult["description"]}
          invalidText={
            orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM && (validationResult["description"] && validationResult["description"].join("; "))
          }
          id="description"
          value={organisation.description}
          labelText={`${i18n.t('page.management.label.description')} ${organisationSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
          maxLength={MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewOrganisation
