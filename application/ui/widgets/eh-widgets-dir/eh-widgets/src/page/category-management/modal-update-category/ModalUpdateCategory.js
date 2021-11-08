import { Modal } from "carbon-components-react"
import { useCallback, useState } from "react"
import UpdateCategory from "./update-category/UpdateCategory"
import {
  editCategory,
  getSingleCategory,
} from "../../../integration/Integration"
import { categorySchema } from "../../../helpers/validation/categorySchema"
import { fillErrors } from "../../../helpers/validation/fillErrors"
import "./modal-update-category.scss"

export const ModalUpdateCategory = ({
  categoryObj,
  open,
  onCloseModal,
  onAfterSubmit,
}) => {
  const [category, setCategory] = useState(categoryObj)
  const [validationResult, setValidationResult] = useState({})

  const onDataChange = useCallback((newCategoryObj) => {
    setCategory(newCategoryObj)
  }, [])

  const onRequestClose = (e) => {
    onCloseModal()
  }

  const getBundleGroupsForACategory = async (categoryId) => {
    const cat = await getSingleCategory(categoryId)
    return cat.category.bundleGroups
  }

  const onRequestSubmit = (e) => {
    ;(async () => {
      let validationError
      await categorySchema
        .validate(category, { abortEarly: false })
        .catch((error) => {
          validationError = fillErrors(error)
        })
      if (validationError) {
        setValidationResult(validationError)
        return //don't send the form
      }
      const bundleGroups = await getBundleGroupsForACategory(
        category.categoryId
      )
      await editCategory(
        {
          name: category.name,
          description: category.description,
          bundleGroups: bundleGroups,
        },
        category.categoryId
      )
      onCloseModal()
      onAfterSubmit()
    })()
  }

  return (
    <Modal
      modalLabel="Edit"
      className="Modal-Update-organization"
      primaryButtonText="Save"
      secondaryButtonText="Cancel"
      open={open}
      onRequestClose={onRequestClose}
      onRequestSubmit={onRequestSubmit}
    >
      <UpdateCategory
        categoryObj={category}
        onDataChange={onDataChange}
        validationResult={validationResult}
      />
    </Modal>
  )
}
