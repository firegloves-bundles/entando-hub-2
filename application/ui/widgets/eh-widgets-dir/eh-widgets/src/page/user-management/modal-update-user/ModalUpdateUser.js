import {Modal} from "carbon-components-react"
import {useCallback, useState} from "react"
import UpdateUser from "./update-user/UpdateUser"
import {
    createAUserForAnOrganisation,
    getAllOrganisations,
    removeUserFromOrganisation
} from "../../../integration/Integration"


export const ModalUpdateUser = ({userObj, open, onCloseModal, onAfterSubmit}) => {

    console.log("Param ModalUpdateUser", userObj)
    const [user, setUser] = useState(userObj)

    const onDataChange = useCallback((userObj) => {
        console.log("Onchange ModalUpdateUser", userObj)
        setUser(userObj)
    }, [])

    const onRequestClose = (e) => {
        onCloseModal()
    }


    const onRequestSubmit = (e) => {
        (async () => {
            //delete all the organisations for the user
            await Promise.all((await getAllOrganisations()).organisationList.map(async (oId) => {
                    await removeUserFromOrganisation(oId.organisationId, user.username)
                }
            ))

            let organisationId = user.organisation.organisationId
            await createAUserForAnOrganisation(organisationId, user.username)


            onCloseModal()
            onAfterSubmit()

        })()
    }

    return (
        <Modal
            modalLabel="Edit"
            primaryButtonText="Save"
            secondaryButtonText="Cancel"
            open={open}
            onRequestClose={onRequestClose}
            onRequestSubmit={onRequestSubmit}>
            <UpdateUser userObj={user} onDataChange={onDataChange}/>
        </Modal>
    )
}
