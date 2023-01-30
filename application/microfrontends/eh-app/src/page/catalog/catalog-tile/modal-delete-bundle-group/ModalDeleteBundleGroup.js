import { Modal } from "carbon-components-react"
import { TrashCan32 } from '@carbon/icons-react'
import { deleteBundleGroupVersion } from "../../../../integration/Integration"

import "./modal-delete-bundle-group.scss"
import i18n from "../../../../i18n";

export const ModalDeleteBundleGroup = ({ apiUrl, open, onCloseModal, bundleGroupVersionId, onAfterSubmit}) => {

    const onRequestClose = (e) => {
        onCloseModal()
    }

    const onRequestDelete = async (e) => {
        await deleteBundleGroupVersion(apiUrl, bundleGroupVersionId);
        onCloseModal()
        onAfterSubmit()
    }

    return (
        <>
            <Modal
                className="Modal-edit-bundle-group"
                modalLabel={i18n.t('component.button.delete')}
                primaryButtonText={i18n.t('component.button.delete')}
                secondaryButtonText={i18n.t('component.button.cancel')}
                open={open}
                onRequestClose={onRequestClose}
                onRequestSubmit={onRequestDelete}
            >
                <div className="Modal-delete-bundle-group">
                    <div className="Modal-delete-bundle-group-wrapper">
                        <TrashCan32 />
                    </div>
                    <div>
                        {i18n.t('modalMsg.deleteBundleMsg')}
                    </div>
                </div>
            </Modal>
        </>
    )
}