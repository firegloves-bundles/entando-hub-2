import {Modal} from "carbon-components-react"
import UpdateBundleGroup from "./update-boundle-group/UpdateBundleGroup"
import {useCallback, useState} from "react"
import {addNewBundle, editBundleGroup} from "../../../../integration/Integration"


export const ModalUpdateBundleGroup = ({bundleGroupId, open, onCloseModal, onAfterSubmit}) => {
    const [bundleGroup, setBundleGroup] = useState({})
    const [passiveModal, setPassiveModal] = useState(false)

    const onDataChange = useCallback((bundleGroup) => {
        setBundleGroup(bundleGroup)
    }, [])

    const onRequestClose = (e) => {
        onCloseModal()
    }

    const onPassiveModal= useCallback((passive)=>{
        setPassiveModal(passive)
    },[])


    const onRequestSubmit = (e) => {
        e.preventDefault()
        (async () => {
            console.log("onRequestSubmit", bundleGroup)
            //create bundle children
            let newChildren = []
            if (bundleGroup.children && bundleGroup.children.length) {
                //call addNewBundle rest api, saving every bundle
                //the call is async in respArray there will be the new bundles id
                let respArray = await Promise.all(bundleGroup.children.map(addNewBundle))
                newChildren = respArray.map(res => res.newBundle.data.bundleId)
            }
            const toSend = {
                ...bundleGroup,
                children: newChildren
            }
            const res = await editBundleGroup(toSend, toSend.bundleGroupId)
            console.log("editBundleGroup", res)
            setBundleGroup({})
            onCloseModal()
            onAfterSubmit()

        })()
    }

    return (
        <Modal
            passiveModal = {passiveModal}
            modalLabel="Edit"
            primaryButtonText="Save"
            secondaryButtonText="Cancel"
            open={open}
            onRequestClose={onRequestClose}
            onRequestSubmit={onRequestSubmit}>
            <UpdateBundleGroup onDataChange={onDataChange} bundleGroupId={bundleGroupId} onPassiveModal={onPassiveModal}/>
        </Modal>
    )
}
