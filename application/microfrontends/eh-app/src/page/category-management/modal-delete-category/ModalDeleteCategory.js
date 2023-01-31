import { Modal } from "carbon-components-react"
import { TrashCan32 } from "@carbon/icons-react"
import "./modal-delete-category.scss"
import { deleteCategory } from "../../../integration/Integration"
import i18n from "../../../i18n"

export const ModalDeleteCategory = ({
    apiUrl,
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
        await deleteCategory( apiUrl, categoryObj.categoryId, categoryObj.name)
        onCloseModal()
        onAfterSubmit()
    }

    if (bundleGroupsLengthOfActiveCategory) {
        return (<Modal
            open
            passiveModal
            onRequestClose={onRequestClose}
            modalHeading={i18n.t("modalMsg.impossibleToRemoveCat")}></Modal>)
    }

    return (<Modal
        className="Modal-Update-organization"
        modalLabel={i18n.t('component.button.delete')}
        primaryButtonText={i18n.t('component.button.delete')}
        secondaryButtonText={i18n.t('component.button.cancel')}
        open={open}
        onRequestClose={onRequestClose}
        onRequestSubmit={onRequestDelete}
    >
        <div className="Modal-delete-category-group">
            <div className="Modal-delete-category-group-wrapper">
                <TrashCan32 />
            </div>
            <div>
                {i18n.t("modalMsg.deleteCat")}
            </div>
        </div>
    </Modal>)
}

export default ModalDeleteCategory;
