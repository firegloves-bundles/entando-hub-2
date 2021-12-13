import {OverflowMenu, OverflowMenuItem} from "carbon-components-react"
import {ModalUpdateBundleGroup} from "../modal-update-bundle-group/ModalUpdateBundleGroup"
import {useState} from "react"
import { ModalDeleteBundleGroup } from "../modal-delete-bundle-group/ModalDeleteBundleGroup"
import { getHigherRole } from "../../../../helpers/helpers"
import {ADMIN, MANAGER, BUNDLE_STATUS} from "../../../../helpers/constants"
import i18n from "../../../../i18n"

const CatalogTileOverflowMenu = ({bundleGroupId, bundleStatus, bundleName,onAfterSubmit}) => {

    const [openModal, setOpenModal] = useState(false)
    const [deleteModal, setDeleteModal] = useState(false)
    const higherRole = getHigherRole()

    const isShowDelete = (higherRole === MANAGER || higherRole === ADMIN) ? true : false;
    const isDeletableBundle = bundleStatus === BUNDLE_STATUS.DELETE_REQ ? true : false

    return (
        <>
            <OverflowMenu>
                <OverflowMenuItem itemText={i18n.t('component.button.edit')} onClick={() => setOpenModal(true)}/>
                {(isShowDelete && isDeletableBundle) && <OverflowMenuItem itemText={i18n.t('component.button.delete')} onClick={() => setDeleteModal(true) }/>}

            </OverflowMenu>
            {openModal && <ModalUpdateBundleGroup open={openModal} bundleGroupId={bundleGroupId} bundleStatus={bundleStatus}
                                     onCloseModal={() => setOpenModal(false)}  onAfterSubmit={onAfterSubmit}/>}

            {deleteModal && <ModalDeleteBundleGroup open={deleteModal} bundleGroupId={bundleGroupId} bundleName={bundleName}
                onCloseModal={() => setDeleteModal(false)} onAfterSubmit={onAfterSubmit} />}
        </>
    )
}

export default CatalogTileOverflowMenu
