import {Button, Modal} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'
import ReactDOM from "react-dom"
import {useState} from "react"
import NewUser from "./new-user/NewUser"
import {createAUserForAnOrganisation} from "../../../integration/Integration"
import "./modal-add-new-user.scss"
import i18n from "../../../i18n"
import { useApiUrl } from "../../../contexts/ConfigContext"

export const ModalAddNewUser = ({ onAfterSubmit }) => {
    const apiUrl = useApiUrl();

    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false)
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data
        const [user, setUser] = useState({})

        const onDataChange = (newUser)=>{
            setUser(newUser)
        }


        const onRequestClose = (e) =>{
            resetData()
            setOpen(false)
        }

        const onRequestOpen = (e) =>{
            setOpen(true)
        }

        const resetData = ()=>{
            setElemKey(((new Date()).getTime()).toString())
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            if (user.organisation &&  user.username) {
                (async () => {
                    let organisationId = user.organisation.organisationId
                    await createAUserForAnOrganisation(apiUrl, organisationId, user.username, 'add')
                    onRequestClose()
                    onAfterSubmit()
                })()
            }
        }

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent open={open} onRequestClose={onRequestClose} onDataChange={onDataChange} onRequestSubmit={onRequestSubmit} elemKey={elemKey}/>,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen}/>}
            </>
        )
    }





    return (
        <ModalStateManager
            renderLauncher={({onRequestOpen}) => (
                <Button onClick={onRequestOpen} renderIcon={Add16}>{i18n.t('page.admin.addUser')}</Button>
            )}>
            {({open, onRequestClose, onDataChange, onRequestSubmit, elemKey}) => (
                <Modal
                    className="ModalAddNewUser"
                    modalLabel={i18n.t('component.button.add')}
                    primaryButtonText={i18n.t('component.button.add')}
                    secondaryButtonText={i18n.t('component.button.cancel')}
                    open={open}
                    onRequestClose={onRequestClose}
                    onRequestSubmit={onRequestSubmit}>
                    <NewUser key={elemKey} onDataChange={onDataChange}/>
                </Modal>
            )}
        </ModalStateManager>
    )
}
