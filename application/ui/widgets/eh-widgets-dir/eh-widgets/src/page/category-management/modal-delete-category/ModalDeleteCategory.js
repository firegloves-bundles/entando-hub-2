import { Modal } from "carbon-components-react"
import { TrashCan32 } from "@carbon/icons-react"
import "./modal-delete-category.scss"
import { deleteCategory } from "../../../integration/Integration"

export const ModalDeleteCategory = ({
    categoryObj,
    open,
    onCloseModal,
    onAfterSubmit,
    categories
}) => {

    const bundleGroupsLengthOfActiveCategory = categories.find(item => item.id === categoryObj.categoryId).bundleGroups.length;

    const onRequestClose = (e) => {
        onCloseModal()
    }

    const onRequestDelete = async (e) => {
        await deleteCategory(categoryObj.categoryId, categoryObj.name)
        onCloseModal()
        onAfterSubmit()
    }

    if (bundleGroupsLengthOfActiveCategory) {
        return (<Modal
            open
            passiveModal
            onRequestClose={onRequestClose}
            modalHeading="This category is already in use."></Modal>)
    }

    return (<Modal
        className="Modal-Update-organization"
        modalLabel="Delete"
        primaryButtonText="Delete"
        secondaryButtonText="Cancel"
        open={open}
        onRequestClose={onRequestClose}
        onRequestSubmit={onRequestDelete}
    >
        <div className="Modal-delete-category-group">
            <div className="Modal-delete-category-group-wrapper">
                <TrashCan32 />
            </div>
            <div>
                Are you sure you want to delete this Category?
            </div>
        </div>
    </Modal>)
}

export default ModalDeleteCategory;