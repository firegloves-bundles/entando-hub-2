import {Modal} from "carbon-components-react"
import {useCallback, useState} from "react"
import UpdateCategory from "./update-category/UpdateCategory"
import {editCategory, getSingleCategory} from "../../../integration/Integration"

import "./modal-update-category.scss"

export const ModalUpdateCategory = ({categoryObj, open, onCloseModal, onAfterSubmit}) => {

    const [category, setCategory] = useState(categoryObj)

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
        (async () => {
            const bundleGroups = await getBundleGroupsForACategory(category.categoryId)
            await editCategory({
                name: category.name,
                description: category.description,
                bundleGroups: bundleGroups
            }, category.categoryId)
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
            onRequestSubmit={onRequestSubmit}>
            <UpdateCategory categoryObj={category} onDataChange={onDataChange}/>
        </Modal>
    )
}
