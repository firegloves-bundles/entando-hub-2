import {Modal} from "carbon-components-react"
import {useCallback, useState} from "react"
import UpdateOrganisation from "./update-organisation/UpdateOrganisation"
import {
    createAUserForAnOrganisation,
    getAllOrganisations,
    removeUserFromOrganisation
} from "../../../integration/Integration"


export const ModalUpdateOrganisation = ({userObj, open, onCloseModal, onAfterSubmit}) => {

    const [user, setUser] = useState(userObj)

    const onDataChange = useCallback((userObj) => {
        setUser(userObj)
    }, [])

    const onRequestClose = (e) => {
        onCloseModal()
    }


    //TODO BE QUERY REFACTORING
    const updateUser = async (user) => {
        //delete all the organisations for the user
        await Promise.all((await getAllOrganisations()).organisationList.map(async (oId) => {
                await removeUserFromOrganisation(oId.organisationId, user.username)
            }
        ))

        let organisationId = user.organisation.organisationId
        await createAUserForAnOrganisation(organisationId, user.username)

    }


    const onRequestSubmit = (e) => {
        (async () => {
            await updateUser(user)
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
            <UpdateOrganisation userObj={user} onDataChange={onDataChange}/>
        </Modal>
    )
}
