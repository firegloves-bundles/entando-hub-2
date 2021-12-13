import {Button, Modal} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'
import ReactDOM from "react-dom"
import {useState} from "react"
import NewOrganisation from "./new-organisation/NewOrganisation"
import {addNewOrganisation} from "../../../integration/Integration"
import "./modal-add-new-organization.scss"
import { organisationSchema } from "../../../helpers/validation/organisationSchema"
import { fillErrors } from "../../../helpers/validation/fillErrors"
import i18n from "../../../i18n"
export const ModalAddNewOrganisation = ({onAfterSubmit}) => {


    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false)
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data
        const [organisation, setOrganisation] = useState({})
        const [validationResult, setValidationResult] = useState({})

        const onDataChange = (newOrganisation)=>{
            setOrganisation(newOrganisation)
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
            (async () => {
                let validationError
                await organisationSchema.validate(organisation, {abortEarly: false}).catch(error => {
                    validationError = fillErrors(error)
                })
                if (validationError) {
                    setValidationResult(validationError)
                    return //don't send the form
                }
                await addNewOrganisation(organisation)
                onRequestClose()
                onAfterSubmit()
            })()
        }

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent validationResult={validationResult} open={open} onRequestClose={onRequestClose} onDataChange={onDataChange} onRequestSubmit={onRequestSubmit} elemKey={elemKey}/>,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen}/>}
            </>
        )
    }





    return (
        <ModalStateManager
            renderLauncher={({onRequestOpen}) => (
                <Button onClick={onRequestOpen} renderIcon={Add16}>{i18n.t('component.button.addOrganisation')}</Button>
            )}>
            {({open, onRequestClose, onDataChange, onRequestSubmit, elemKey, validationResult}) => (
                <Modal
                    modalLabel={i18n.t('component.button.add')}
                    className="Modal-Add-New-organization"
                    primaryButtonText={i18n.t('component.button.add')}
                    secondaryButtonText={i18n.t('component.button.cancel')}
                    open={open}
                    onRequestClose={onRequestClose}
                    onRequestSubmit={onRequestSubmit}>
                    <NewOrganisation key={elemKey} onDataChange={onDataChange} validationResult={validationResult}/>
                </Modal>
            )}
        </ModalStateManager>
    )
}
