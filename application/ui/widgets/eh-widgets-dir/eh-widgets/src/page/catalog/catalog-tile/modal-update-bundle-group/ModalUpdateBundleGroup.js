import {Modal} from "carbon-components-react";
import UpdateBundleGroup from "./update-boundle-group/UpdateBundleGroup";
import {addNewBundle, addNewBundleGroup} from "../../../../integration/Integration";
import {useState} from "react";


export const ModalUpdateBundleGroup = ({bundleGroupId, open, onCloseModal}) => {
    const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data
    const [bundleGroup, setBundleGroup] = useState({})

    const onDataChange = (bundleGroup)=>{
        setBundleGroup(bundleGroup)
    }

    const onRequestClose = (e) =>{
        onCloseModal()
        resetData()
    }

    const resetData = ()=>{
        setElemKey(((new Date()).getTime()).toString())
    }

    const onRequestSubmit = (e) => {
        e.preventDefault();
        console.log(bundleGroup)
        (async () => {
/*
            //create bundle children
            let newChildren = []
            if(bundleGroup.children && bundleGroup.children.length) {
                let respArray = await Promise.all(bundleGroup.children.map(addNewBundle))
                console.log("respArray", respArray)
                newChildren = respArray.map(res => res.newBundle.data.bundleId)
            }
            console.log("newChildren", newChildren)
            const toSend = {
                ...bundleGroup,
                children: newChildren
            }
            const res = await addNewBundleGroup(toSend)
            console.log("addNewBundleGroup", res)
            setBundleGroup(toSend)
            resetData()
            afterSubmit()
*/

        })()
    };

    return (
        <Modal
            modalLabel="Edit"
            primaryButtonText="Save"
            secondaryButtonText="Cancel"
            open={open}
            onRequestClose={onRequestClose}
            onRequestSubmit={onRequestSubmit}>
            <UpdateBundleGroup key={elemKey} onDataChange={onDataChange} bundleGroupId={bundleGroupId}/>
        </Modal>
    )
}
