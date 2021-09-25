import {useCallback, useEffect, useState} from "react"
import {Content, Select, SelectItem, TextArea, TextInput,} from "carbon-components-react"
import {getAllCategories} from "../../../../integration/Integration"
import AddBundleToBundleGroup from "./add-bundle-to-bundle-group/AddBundleToBundleGroup"
import {getProfiledNewSelecSatustInfo} from "../../../../helpers/profiling"
import {getHigherRole} from "../../../../helpers/helpers"

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}
 */

const NewBundleGroup = ({onDataChange}) => {
    const [selectOptions, setSelectOptions] = useState([])
    const [categories, setCategories] = useState([])
    const [newBundleGroup, setNewBundleGroup] = useState({
        name: "",
        description: "",
        descriptionImage: "",
        documentationUrl: "",
        status: "",
        children: [],
        categories: [],
    })

    const changeNewBundleGroup = (field, value) => {
        const newObj = {
            ...newBundleGroup,
        }
        newObj[field] = value
        setNewBundleGroup(newObj)
        onDataChange(newObj)
    }

    const createSelectOptionsForRole = useCallback(() => {
        const selectValuesInfo = getProfiledNewSelecSatustInfo(getHigherRole())
        const options = selectValuesInfo.values.map((curr, index) => <SelectItem key={index} value={curr.value} text={curr.text}/>)
        setSelectOptions(options)
    }, [])


    useEffect(() => {
        let isMounted = true
        const init = async () => {
            const res = await getAllCategories()
            if (isMounted) {
                createSelectOptionsForRole()
                setCategories(res.categoryList)
                //default values
                let defaultCategoryId = res.categoryList.filter(cat=>cat.name==="Solution Template")[0].categoryId
                const newObj ={
                    name: "",
                    description: "",
                    descriptionImage: "",
                    documentationUrl: "",
                    children: [],
                    categories: [defaultCategoryId],
                    status: "NOT_PUBLISHED"
                }

                setNewBundleGroup(newObj)
            }
        }
        init()
        return () => { isMounted = false }

    }, [createSelectOptionsForRole])

    let selectItems_Category = categories.map((category) => {
        return (
            <SelectItem
                key={category.categoryId}
                value={category.categoryId}
                text={category.name}
            />
        )
    })



    const nameChangeHandler = (e) => {
        changeNewBundleGroup("name", e.target.value)
    }

    const categoryChangeHandler = (e) => {
        changeNewBundleGroup("categories", [e.target.value])
    }

    const documentationChangeHandler = (e) => {
        changeNewBundleGroup("documentationUrl", e.target.value)
    }

    const versionChangeHandler = (e) => {
        // const value = e.target.value
        // setNewBundleGroup(prev => {
        //   return {
        //     ...prev,
        //     version: value
        //   }
        // })
        //changeNewBundleGroup("version", e.target.value)
    }

    const statusChangeHandler = (e) => {
        changeNewBundleGroup("status", e.target.value)
    }

    const descriptionChangeHandler = (e) => {
        changeNewBundleGroup("description", e.target.value)
    }

    /*
        NewBundleList will contain the array of bundle object
    */
    const onAddOrRemoveBundleFromList = (newBundleList) => {
        //Warning in NewBundleGroup children field there will be the whole bundle object not
        //only the id
        changeNewBundleGroup("children", newBundleList)
    }

    return (
        <>
            <Content>
                <TextInput id="name" labelText="Name" onChange={nameChangeHandler} />
                <Select id="category" labelText="Categories" value={newBundleGroup.categories[0]} onChange={categoryChangeHandler} >{selectItems_Category}</Select>
                <TextInput id="documentation" labelText="Documentation Address" onChange={documentationChangeHandler} />
                <TextInput id="version" labelText="Version" onChange={versionChangeHandler}  />
                <Select id="status" labelText="Status" value={newBundleGroup.status} onChange={statusChangeHandler} >{selectOptions}</Select>
                <TextArea id="description" labelText="Description" onChange={descriptionChangeHandler} cols={50} rows={4} />
                {/*
                    Renders the bundle list of children allowing
                    the user to add/delete them. Whenever a user
                    performs an action on that list onAddOrRemoveBundleFromList will be called
                */}
                <AddBundleToBundleGroup onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}/>
            </Content>
        </>
    )
}

export default NewBundleGroup
