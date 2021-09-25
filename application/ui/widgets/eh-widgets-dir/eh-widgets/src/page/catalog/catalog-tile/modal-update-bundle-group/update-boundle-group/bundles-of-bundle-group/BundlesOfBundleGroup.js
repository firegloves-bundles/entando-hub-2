import {useEffect, useState} from "react"
import {Button, Tag, TextInput,} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'

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
        name: gitRepoAddress.substring(gitRepoAddress.lastIndexOf("/") + 1, gitRepoAddress.lastIndexOf(".")),
        gitRepoAddress
    } : {
        name: "",
        gitRepoAddress: ""
    }
}

const BundleList = ({children = []}) => {
    console.log("BundleList", children)
    const elemList = children.map(bundle => bundle.gitRepoAddress).map(parseGitRepoAddr).map((childrenInfo, index) =>
        <li key={index.toString()}><Tag><a href={childrenInfo.gitRepoAddress}
                                           target={"_new"}>{childrenInfo.name}</a></Tag></li>)

    return (<div>
        List of Bundles
        <ul>{elemList}</ul>
    </div>)

}


const BundlesOfBundleGroup = ({onAddOrRemoveBundleFromList, initialBundleList, disabled = false}) => {
    console.log("initialBundleList", initialBundleList)

    useEffect(()=>{
        setBundleList(initialBundleList)
    },[initialBundleList])

    const [bundleList, setBundleList] = useState([])
    const [gitRepo, setGitRepo] = useState("")

    const onChangeHandler = (e) => {
        const value = e.target.value
        setGitRepo(value)
    }

    const onAddBundle = (e) => {
        if (gitRepo === "") return
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
            <TextInput value={gitRepo} onChange={onChangeHandler} {...textInputProps} />
            <Button disabled={disabled} onClick={onAddBundle} renderIcon={Add16}>Add</Button>
            <BundleList children={bundleList}/>
        </>
    )

}


export default BundlesOfBundleGroup

