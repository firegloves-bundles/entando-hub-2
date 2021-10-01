import {useState} from "react"
import {Button, Tag, TextInput, Row, Column} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'
import './add-boundle-to-bundle-group.scss'
/*
BUNDLE:
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
} */

const parseGitRepoAddr = (gitRepoAddress) => {
  return gitRepoAddress ? {
    name: gitRepoAddress.substring(gitRepoAddress.lastIndexOf("/") + 1,
        gitRepoAddress.lastIndexOf(".")),
    gitRepoAddress
  } : {
    name: "",
    gitRepoAddress: ""
  }
}

const BundleList = ({children}) => {
  const elemList = children.map(bundle => bundle.gitRepoAddress).map(
      parseGitRepoAddr).map((childrenInfo, index) =>
      <li key={index.toString()}>
        <Tag>
          <a href={childrenInfo.gitRepoAddress}
             target={"_new"}>{childrenInfo.name}
          </a>
        </Tag>
      </li>)

  return (
      <div className="AddBundleToBundleGroup-Bundle-list">
        {/*<p>List of Bundles</p>*/}
        <ul className="AddBundleToBundleGroup-Bundle-list-ul">
          {elemList}
        </ul>
      </div>
  )
}

const AddBundleToBundleGroup = ({
  onAddOrRemoveBundleFromList,
  initialBundleList = []
}) => {

  const [bundleList, setBundleList] = useState(initialBundleList)
  const [gitRepo, setGitRepo] = useState("")

  const onChangeHandler = (e) => {
    const value = e.target.value
    setGitRepo(value)
  }

  const onAddBundle = (e) => {
    if (gitRepo === "") {
      return
    }
    let newBundleList = [...bundleList, {
      name: parseGitRepoAddr(gitRepo).name,
      description: gitRepo,
      gitRepoAddress: gitRepo,
      dependencies: [],
      bundleGroups: []
    }]
    setBundleList(newBundleList)
    onAddOrRemoveBundleFromList(newBundleList)
    setGitRepo("")
  }

  const textInputProps = {
    id: "bundle",
    labelText: "Add Url Bundle",
  }

  return (
      <>
        <Row>
          <Column sm={16} md={8} lg={8}>
            <TextInput value={gitRepo}
                       onChange={onChangeHandler} {...textInputProps} />
          </Column>
          <Column sm={16} md={8} lg={8}>
            <div className="AddBundleToBundleGroup-add-button">
              <Button onClick={onAddBundle} renderIcon={Add16}>
                Add
              </Button>
            </div>
          </Column>
          <Column sm={16} md={8} lg={8}>
            <div>
              <BundleList children={bundleList}/>
            </div>
          </Column>
        </Row>
      </>
  )

}

export default AddBundleToBundleGroup

