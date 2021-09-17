import {useEffect, useState} from "react";
import {Content, Select, SelectItem, TextArea, TextInput,} from "carbon-components-react";
import {getAllCategories} from "../../../../integration/Integration";
import AddBundleToBundleGroup from "./add-bundle-to-bundle-group/AddBundleToBundleGroup";

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
    const [categories, setCategories] = useState([]);
    const [newBundleGroup, setNewBundleGroup] = useState({
        name: "",
        description: "",
        descriptionImage: "",
        documentationUrl: "",
        status: "",
        children: [],
        categories: [],
    });

    const changeNewBundleGroup = (field, value) => {
        const newObj = {
            ...newBundleGroup,
        }
        newObj[field] = value
        setNewBundleGroup(newObj)
        onDataChange(newObj)
    }

    useEffect(() => {
        let isMounted = true;
        const init = async () => {
            const res = await getAllCategories();
            if (isMounted) {
                setCategories(res.categoryList);
            }
        };
        init();
        return () => { isMounted = false }

    }, []);

    let selectItems_Category = categories.map((category) => {
        return (
            <SelectItem
                key={category.categoryId}
                value={category.categoryId}
                text={category.name}
            />
        );
    });
    selectItems_Category.unshift(
        <SelectItem
            key="-1"
            disabled
            hidden
            value="placeholder-item"
            text="Choose an option"
        />
    );


    const selectProps_Category = {
        id: "category",
        defaultValue: "placeholder-item",
        labelText: "Category",
    };

    const selectItems_Status = [
        <SelectItem
            key="-1"
            disabled
            hidden
            value="placeholder-item"
            text="Choose an option"
        />,
        <SelectItem key="0" value="NOT_PUBLISHED" text="Not Published"/>,
        <SelectItem key="1" value="PUBLISHED" text="Published"/>,
    ];

    const selectProps_Status = {
        id: "status",
        defaultValue: "placeholder-item",
        labelText: "Status",
    };

    const textInputProps_Name = {
        id: "name",
        labelText: "Name",
    };

    const textInputProps_Documentation = {
        id: "documentation",
        labelText: "Documentation Address",
    };

    const textInputProps_Version = {
        id: "version",
        labelText: "Version",
    };

    const textAreaProps_Description = {
        id: "description",
        labelText: "Description",
        cols: 50,
        rows: 4,
    };

    const fileUploaderProps_Images = {
        id: "images",
        buttonLabel: "Add Files",
        labelDescription:
            "Max file size is 500kb. Max 4 images. Supported file types are .jpg, .png, and .pdf",
    };

    const fieldsetFileUploaderProps_Images = {
        legendText: "Upload Images",
    };

    const imagesChangeHandler = (e) => {
        changeNewBundleGroup("descriptionImage", e.target.value)
    }

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
        // const value = e.target.value;
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

    const onAddOrRemoveBundleFromList = (newBundleList) => {
        changeNewBundleGroup("children", newBundleList)
    }

    console.log("NBG", newBundleGroup)

    return (
        <>
            <Content>
                <TextInput onChange={nameChangeHandler} {...textInputProps_Name} />
                <Select onChange={categoryChangeHandler} {...selectProps_Category}>{selectItems_Category}</Select>
                <TextInput onChange={documentationChangeHandler} {...textInputProps_Documentation} />
                <TextInput onChange={versionChangeHandler} {...textInputProps_Version} />
                <Select onChange={statusChangeHandler} {...selectProps_Status}>{selectItems_Status}</Select>
                <TextArea onChange={descriptionChangeHandler} {...textAreaProps_Description} />
                <AddBundleToBundleGroup onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}/>
            </Content>
        </>
    );
};

export default NewBundleGroup;
