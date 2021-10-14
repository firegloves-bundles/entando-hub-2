import {OverflowMenu, OverflowMenuItem} from "carbon-components-react"
import {ModalUpdateBundleGroup} from "../modal-update-bundle-group/ModalUpdateBundleGroup"
import {useState} from "react"

const CatalogTileOverflowMenu = ({bundleGroupId, onAfterSubmit}) => {

    const [openModal, setOpenModal] = useState(false)
    return (
        <>
            <OverflowMenu>
                <OverflowMenuItem itemText="Edit" onClick={() => setOpenModal(true)}/>
            </OverflowMenu>
            {openModal && <ModalUpdateBundleGroup open={openModal} bundleGroupId={bundleGroupId}
                                     onCloseModal={() => setOpenModal(false)}  onAfterSubmit={onAfterSubmit}/>}
        </>
    )
}

export default CatalogTileOverflowMenu
