import { Modal } from "carbon-components-react"
import { TrashCan32 } from '@carbon/icons-react'
import { deleteBundle } from "../../../../integration/Integration"
import { MODAL_LABELS, BUTTON_LABELS } from "../../../../helpers/constants";

import "./modal-delete-bundle-group.scss"

export const ModalDeleteBundleGroup = ({ open, onCloseModal, bundleName, bundleGroupId, onAfterSubmit}) => {

    const onRequestClose = (e) => {
        onCloseModal()
    }

    const onRequestDelete = async (e) => {
        await deleteBundle(bundleGroupId, bundleName);
        onCloseModal()
        onAfterSubmit()
    }

    return (
        <>
            <Modal
                className="Modal-edit-bundle-group"
                modalLabel={BUTTON_LABELS.DELETE}
                primaryButtonText={BUTTON_LABELS.DELETE}
                secondaryButtonText={BUTTON_LABELS.CANCEL}
                open={open}
                onRequestClose={onRequestClose}
                onRequestSubmit={onRequestDelete}
            >
                <div className="Modal-delete-bundle-group">
                    <div className="Modal-delete-bundle-group-wrapper">
                        <TrashCan32 />
                    </div>
                    <div>
                        {MODAL_LABELS.DELETE_BUNDLE_MSG}
                    </div>
                </div>
            </Modal>
        </>
    )
}