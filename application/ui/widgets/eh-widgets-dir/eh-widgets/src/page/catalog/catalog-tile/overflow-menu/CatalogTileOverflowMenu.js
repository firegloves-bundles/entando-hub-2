import {OverflowMenu, OverflowMenuItem} from "carbon-components-react"
import {ModalUpdateBundleGroup} from "../modal-update-bundle-group/ModalUpdateBundleGroup"
import {useState} from "react"
import { ModalDeleteBundleGroup } from "../modal-delete-bundle-group/ModalDeleteBundleGroup"
import { getHigherRole } from "../../../../helpers/helpers"
import {ADMIN, MANAGER, BUNDLE_STATUS } from "../../../../helpers/constants"
import { ModalAddNewBundleGroupVersion } from "../modal-add-new-bundle-group-version/ModalAddNewBundleGroupVersion"
import i18n from "../../../../i18n";
import { useHistory } from "react-router-dom";

const CatalogTileOverflowMenu = ({bundleGroupId, bundleStatus, bundleName, onAfterSubmit, bundleGroup, isVersionsPage}) => {

    const [openModal, setOpenModal] = useState(false)
    const [deleteModal, setDeleteModal] = useState(false)

    const [addBundleGroupVersionModal, setAddBundleGroupVersionModal] = useState(false);

    const higherRole = getHigherRole()

    const isShowDelete = (higherRole === MANAGER || higherRole === ADMIN) ? true : false;
    const isDeletableBundle = bundleStatus === BUNDLE_STATUS.DELETE_REQ ? true : false
    const isAddVersionOptionVisible = (bundleStatus === BUNDLE_STATUS.PUBLISHED && !isVersionsPage) ? true : false;
    const isViewVersionOptionVisible = (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.NOT_PUBLISHED) && !isVersionsPage ? true : false;

    const history = useHistory()

    const handleViewVersionsClick = () => {
        history.push("/versions/" + bundleGroupId)
    }

    return (
        <>
            <OverflowMenu>
                
                <OverflowMenuItem itemText={i18n.t('component.button.edit')} onClick={() => setOpenModal(true)}/>
                
                {(isShowDelete && isDeletableBundle) && <OverflowMenuItem itemText={i18n.t('component.button.delete')} onClick={() => setDeleteModal(true) }/>}

                {/* Show Add Version option */}
                {isAddVersionOptionVisible &&
                    <OverflowMenuItem itemText={i18n.t('component.button.newVersion')} onClick={() => setAddBundleGroupVersionModal(true)}/>}

                {/* Show View Versions option */}
                {isViewVersionOptionVisible && 
                    <OverflowMenuItem itemText={i18n.t('component.button.viewVersions')} onClick={handleViewVersionsClick} />}

            </OverflowMenu>

            {openModal && <ModalUpdateBundleGroup bundleGroupObj={bundleGroup} open={openModal} bundleGroupId={bundleGroupId} bundleStatus={bundleStatus}
                                     onCloseModal={() => setOpenModal(false)}  onAfterSubmit={onAfterSubmit}/>}

            {/* {deleteModal && <ModalDeleteBundleGroup open={deleteModal} bundleGroupId={bundleGroupId} bundleName={bundleName}
                onCloseModal={() => setDeleteModal(false)} onAfterSubmit={onAfterSubmit} />} */}

            {deleteModal && <ModalDeleteBundleGroup open={deleteModal} bundleGroupVersionId={bundleGroup && bundleGroup.bundleGroupVersionId} bundleName={bundleName}
                onCloseModal={() => setDeleteModal(false)} onAfterSubmit={onAfterSubmit} />}

            {/* Add bundle group version modal */}
            {addBundleGroupVersionModal && <ModalAddNewBundleGroupVersion theBundleGroup={bundleGroup} open={addBundleGroupVersionModal}
                onCloseModal={() => setAddBundleGroupVersionModal(false)} onAfterSubmit={onAfterSubmit} />}

        </>
    )
}

export default CatalogTileOverflowMenu
