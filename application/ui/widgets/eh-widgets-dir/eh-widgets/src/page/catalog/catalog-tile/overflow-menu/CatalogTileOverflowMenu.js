import {OverflowMenu, OverflowMenuItem} from "carbon-components-react"
import {ModalUpdateBundleGroup} from "../modal-update-bundle-group/ModalUpdateBundleGroup"
import {useState} from "react"
import { ModalDeleteBundleGroup } from "../modal-delete-bundle-group/ModalDeleteBundleGroup"
import { getHigherRole } from "../../../../helpers/helpers"
import {ADMIN, MANAGER, BUNDLE_STATUS, MENU_OPTIONS } from "../../../../helpers/constants"
import { ModalAddNewBundleGroupVersion } from "../modal-add-new-bundle-group-version/ModalAddNewBundleGroupVersion"
import i18n from "../../../../i18n"

const CatalogTileOverflowMenu = ({bundleGroupId, bundleStatus, bundleName, onAfterSubmit, bundleGroup}) => {

    const [openModal, setOpenModal] = useState(false)
    const [deleteModal, setDeleteModal] = useState(false)

    const [addBundleGroupVersionModal, setAddBundleGroupVersionModal] = useState(false);
    // const [viewBundleGroupVersionModal, setViewBundleGroupVersionModal] = useState(false);

    const higherRole = getHigherRole()

    const isShowDelete = (higherRole === MANAGER || higherRole === ADMIN) ? true : false;
    const isDeletableBundle = bundleStatus === BUNDLE_STATUS.DELETE_REQ ? true : false
    const isAddVersionOptionVisible = bundleStatus === BUNDLE_STATUS.PUBLISHED ? true : false;
    const isViewVersionOptionVisible = (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.NOT_PUBLISHED) ? true : false;

    return (
        <>
            <OverflowMenu>
                
                <OverflowMenuItem itemText={i18n.t('component.button.edit')} onClick={() => setOpenModal(true)}/>
                
                {(isShowDelete && isDeletableBundle) && <OverflowMenuItem itemText={i18n.t('component.button.delete')} onClick={() => setDeleteModal(true) }/>}
                {/* Show Add Version option */}
                {isAddVersionOptionVisible &&
                    <OverflowMenuItem itemText={MENU_OPTIONS.ADD_BUNDLE_GROUP_VERSION} onClick={() => setAddBundleGroupVersionModal(true)}/>}

                {/* Show View Versions option */}
                {isViewVersionOptionVisible && 
                    <OverflowMenuItem itemText={MENU_OPTIONS.VIEW_BUNDLE_GROUP_VERSIONS} onClick={() => {}}/>}

            </OverflowMenu>

            {openModal && <ModalUpdateBundleGroup open={openModal} bundleGroupId={bundleGroupId} bundleStatus={bundleStatus}
                                     onCloseModal={() => setOpenModal(false)}  onAfterSubmit={onAfterSubmit}/>}

            {deleteModal && <ModalDeleteBundleGroup open={deleteModal} bundleGroupId={bundleGroupId} bundleName={bundleName}
                onCloseModal={() => setDeleteModal(false)} onAfterSubmit={onAfterSubmit} />}

            {/* Add bundle group version */}
            {addBundleGroupVersionModal && <ModalAddNewBundleGroupVersion theBundleGroup={bundleGroup} open={addBundleGroupVersionModal}
                onCloseModal={() => setAddBundleGroupVersionModal(false)} onAfterSubmit={onAfterSubmit} />}

        </>
    )
}

export default CatalogTileOverflowMenu
