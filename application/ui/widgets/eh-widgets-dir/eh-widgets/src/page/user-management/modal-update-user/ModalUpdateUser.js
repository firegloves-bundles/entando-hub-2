import {Loading, Modal} from "carbon-components-react"
import {useCallback, useEffect, useState} from "react"
import UpdateUser from "./update-user/UpdateUser"
import {
    createAUserForAnOrganisation,
    getAllOrganisations,
    removeUserFromOrganisation
} from "../../../integration/Integration"


export const ModalUpdateUser = ({userObj, open, onCloseModal, onAfterSubmit}) => {

    const [user, setUser] = useState(userObj)
    const [visible, setVisible] = useState(false)
    const [organisations, setOrganisations] = useState([])



    useEffect(() => {
        (async () => {
            //TODO load the organisation from the db together with the userobj
            const organisations = (await getAllOrganisations()).organisationList
            setOrganisations(organisations)
            const userObjWithDefaultOrganisation = userObj.organisation ? userObj : {
                ...userObj,
                organisation: organisations[0]
            }
            setUser(userObjWithDefaultOrganisation)
            setVisible(true)
        })()
    }, [userObj])


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
        <>
            {!visible && <Loading/>}
            <Modal
                style={{display: !visible?"none":""}}
                modalLabel="Edit"
                primaryButtonText="Save"
                secondaryButtonText="Cancel"
                open={open}
                onRequestClose={onRequestClose}
                onRequestSubmit={onRequestSubmit}>
                <UpdateUser userObj={user} organisations={organisations} onDataChange={onDataChange}/>
            </Modal>
        </>
    )
}
