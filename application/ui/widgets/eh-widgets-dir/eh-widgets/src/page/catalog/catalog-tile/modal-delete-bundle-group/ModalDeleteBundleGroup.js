import { Modal } from "carbon-components-react"
import { TrashCan32 } from '@carbon/icons-react'
import { deleteBundle } from "../../../../integration/Integration"

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
                modalLabel="Delete"
                primaryButtonText="Delete"
                // danger
                secondaryButtonText="Cancel"
                open={open}
                onRequestClose={onRequestClose}
                onRequestSubmit={onRequestDelete}
            >
                <div className="Modal-delete-bundle-group">
                    <div className="Modal-delete-bundle-group-wrapper">
                        <TrashCan32 />
                    </div>
                    <div>
                        Are you sure you want to delete this bundle?
                    </div>
                </div>
            </Modal>
        </>
    )
}