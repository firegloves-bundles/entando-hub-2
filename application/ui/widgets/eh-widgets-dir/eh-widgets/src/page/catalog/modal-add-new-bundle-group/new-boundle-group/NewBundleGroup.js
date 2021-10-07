import {useCallback, useEffect, useState} from "react"
import {
  Content,
  Select,
  SelectItem,
  TextArea,
  TextInput,
  Column,
  Grid,
  Row
} from "carbon-components-react"
import {getAllCategories} from "../../../../integration/Integration"
import AddBundleToBundleGroup from "./add-bundle-to-bundle-group/AddBundleToBundleGroup"
import {getProfiledNewSelecSatustInfo} from "../../../../helpers/profiling"
import {getHigherRole} from "../../../../helpers/helpers"
import {getCurrentUserOrganisation} from "../../../../integration/api-adapters"
import IconUploader from "../../catalog-tile/modal-update-bundle-group/update-boundle-group/icon-uploader/IconUploader";

import './new-boundle-group.scss'
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

/**
 *
 * @param onDataChange callback functions (newBundleGroup)=>{} newBundleGroup will contain the updated object
 */
const NewBundleGroup = ({onDataChange}) => {
    const [userOrganisation, setUserOrganisation] = useState({organisationId: "", name: ""})
    const [selectOptions, setSelectOptions] = useState([])
    const [categories, setCategories] = useState([])
    const [newBundleGroup, setNewBundleGroup] = useState({
        name: "",
        description: "",
        descriptionImage: "",
        documentationUrl: "",
        status: "",
        children: [],
        categories: []
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
        const options = selectValuesInfo.values.map((curr, index) => <SelectItem key={index} value={curr.value}
                                                                                 text={curr.text}/>)
        setSelectOptions(options)
    }, [])


    useEffect(() => {
        let isMounted = true
        const init = async () => {
            const res = await getAllCategories()
            const userOrganisation = await getCurrentUserOrganisation()
            if (isMounted) {
                createSelectOptionsForRole()
                setCategories(res.categoryList)
                if (userOrganisation) setUserOrganisation(userOrganisation)
                //default values
                let defaultCategoryId = res.categoryList.filter(cat => cat.name === "Solution Template")[0].categoryId
                const newObj = {
                    name: "",
                    description: "",
                    descriptionImage: "",
                    documentationUrl: "",
                    children: [],
                    categories: [defaultCategoryId],
                    status: "NOT_PUBLISHED",
                    organisationId: userOrganisation ? userOrganisation.organisationId : undefined
                }

                setNewBundleGroup(newObj)
            }
        }
        init()
        return () => {
            isMounted = false
        }

    }, [createSelectOptionsForRole])

    const fileUploaderProps_Images = {
        id: "images",
        buttonLabel: "Add Files",
        labelDescription:
            "Max file size is 500kb. Supported file types are .jpg, .png, and .pdf",
    }

    let selectItems_Category = categories.map((category) => {
        return (
            <SelectItem
                key={category.categoryId}
                value={category.categoryId}
                text={category.name}
            />
        )
    })

    const convertToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const fileReader = new FileReader()
            fileReader.readAsDataURL(file)
            fileReader.onload = () => {
                resolve(fileReader.result)
            }
            fileReader.onerror = (error) => {
                reject(error)
            }
        })
    }

    const imagesChangeHandler = (e) => {
        (async () => {
            const file = e.target.files[0]
            const base64 = await convertToBase64(file)
            changeNewBundleGroup("descriptionImage", base64)
        })()
    }
    const imagesDeleteHandler = (e) => {
        changeNewBundleGroup("descriptionImage", "")

    }
    const categoryChangeHandler = (e) => {
        changeNewBundleGroup("categories", [e.target.value])
    }

    const onChangeHandler = (e,fieldName) => {
        changeNewBundleGroup(fieldName, e.target.value)
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
        <Content className="New-bundle-group">
          <Grid>
            <Row>
              <Column sm={16} md={8} lg={8}>
                <IconUploader descriptionImage={newBundleGroup.descriptionImage} disabled={false} fileUploaderProps_Images={fileUploaderProps_Images} onImageChange={imagesChangeHandler} onImageDelete={imagesDeleteHandler}/>
              </Column>
            </Row>
            <Row>
              <Column sm={16} md={8} lg={8}>
                <TextInput id="name" labelText="Name" onChange={(e)=>onChangeHandler(e,"name")}/>
              </Column>

              <Column sm={16} md={8} lg={8}>
                <Select id="category" labelText="Categories" value={newBundleGroup.categories[0]}
                        onChange={categoryChangeHandler}>{selectItems_Category}</Select>
              </Column>
              <Column sm={16} md={8} lg={8}>
                 <TextInput id="documentation" labelText="Documentation Address" onChange={(e)=>onChangeHandler(e,"documentationUrl")}/>
              </Column>
              <Column sm={16} md={8} lg={8}>
                 <TextInput id="version" labelText="Version" onChange={(e)=>onChangeHandler(e,"version")}/>
              </Column>
              <Column sm={16} md={16} lg={16}>
                 <TextInput disabled={true} id="organisation" labelText="Organisation" value={userOrganisation.name}/>
              </Column>
              <Column sm={16} md={16} lg={16}>
                <Select id="status" labelText="Status" value={newBundleGroup.status}
                        onChange={(e)=>onChangeHandler(e,"status")}>{selectOptions}</Select>
              </Column>

              <Column sm={16} md={16} lg={16}>
                <AddBundleToBundleGroup onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}/>
              </Column>

              <Column sm={16} md={16} lg={16}>
                <TextArea id="description" labelText="Description" onChange={(e)=>onChangeHandler(e,"description")} cols={50}
                          rows={4}/>
              </Column>
              {/*
                    Renders the bundle list of children allowing
                    the user to add/delete them. Whenever a user
                    performs an action on that list onAddOrRemoveBundleFromList will be called
                */}
            </Row>
          </Grid>
        </Content>
      </>
  )
}

export default NewBundleGroup
