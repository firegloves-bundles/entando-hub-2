import {Button, CodeSnippet, Modal} from "carbon-components-react"
import ReactDOM from "react-dom"
import {useState} from "react"
import "./modal-install-information.scss"
import i18n from "../../../i18n"

const InstallationInfo = ({bundleGroup, children}) => {
    const elemList = children.map((bundle, index)=>(
        <CodeSnippet key={index.toString()} type="multi" feedback="Copied to clipboard">
            {`ent ecr deploy --repo=${bundle.gitRepoAddress}`}
        </CodeSnippet>
    ))

  return (
      <div className="Modal-install-code">
        <div className="Modal-install-code">
          {elemList}
        </div>
        <div>
            Notes: See the <a href="https://developer.entando.com/cli.html" target="_blank" rel="noopener noreferrer">CLI documentation</a> for
            additional information about the Entando CLI. See the <a href="https://developer.entando.com/hub.html" target="_blank" rel="noopener noreferrer">
            Hub documentation</a> to connect an Entando App Builder to the Hub and install bundles without the CLI.
        </div>
      </div>
  )
}


export const ModalInstallInformation = (props) => {
    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false)
        const [bundleGroup] = useState(props.bundleGroup)
        const [children] = useState(props.children)

        const onRequestClose = (e) => {
            setOpen(false)
        }

        const onRequestOpen = (e) => {
            setOpen(true)
        }

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent open={open} onRequestClose={onRequestClose} bundleGroup={bundleGroup} children={children}/>,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen}/>}
            </>
        )
    }


    return (
      <ModalStateManager
          renderLauncher={({onRequestOpen}) => (
              <Button onClick={onRequestOpen}>{i18n.t('component.button.install')}</Button>
          )}>
        {({open, onRequestClose, bundleGroup, children}) => (
            <Modal
                className="Modal-install-information"
                modalLabel={"How to install " + bundleGroup.name}
                passiveModal
                open={open}
                onRequestClose={onRequestClose}>
              <InstallationInfo bundleGroup={bundleGroup} children={children}/>
            </Modal>
        )}
      </ModalStateManager>
  )
}
