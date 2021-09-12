import {useEffect, useState} from "react";
import {
    Form,
    FormGroup,
    FileUploader,
    Button,
    TextArea,
    TextInput,
    Select,
    SelectItem,
    Content,
} from "carbon-components-react";
import {getAllCategories, addNewBundleGroup, addNewBundle} from "../../../integration/Integration";
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

const NewBundleGroup = () => {
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

    useEffect(() => {
        const init = async () => {
            const res = await getAllCategories();
            setCategories(res.categoryList);
        };

        init();
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

    const submitHandler = (e) => {
        (async () => {
            e.preventDefault();
            //create bundle children
            let respArray = await Promise.all(newBundleGroup.children.map(addNewBundle))
            console.log("respArray", respArray)
            const newChildren = respArray.map(res => res.newBundle.data.bundleId)
            console.log("newChildren", newChildren)
            const toSend = {
                ...newBundleGroup,
                children: newChildren
            }
            setNewBundleGroup(toSend)
            const res = addNewBundleGroup(toSend)
            await console.log(res)

        })()
    };

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
        const value = e.target.value;
        setNewBundleGroup(prev => {
            return {
                ...prev,
                descriptionImage: value
            }
        })
    }

    const nameChangeHandler = (e) => {
        const value = e.target.value;
        setNewBundleGroup(prev => {
            return {
                ...prev,
                name: value
            }
        })
    }

    const categoryChangeHandler = (e) => {
        const value = e.target.value;
        setNewBundleGroup(prev => {
            return {
                ...prev,
                categories: [value]
            }
        })
    }

    const documentationChangeHandler = (e) => {
        const value = e.target.value;
        setNewBundleGroup(prev => {
            return {
                ...prev,
                documentationUrl: value
            }
        })
    }

    const versionChangeHandler = (e) => {
        // const value = e.target.value;
        // setNewBundleGroup(prev => {
        //   return {
        //     ...prev,
        //     version: value
        //   }
        // })
    }

    const statusChangeHandler = (e) => {
        const value = e.target.value;
        setNewBundleGroup(prev => {
            return {
                ...prev,
                status: value
            }
        })
    }

    const descriptionChangeHandler = (e) => {
        const value = e.target.value;
        setNewBundleGroup(prev => {
            return {
                ...prev,
                description: value
            }
        })
    }

    const onAddOrRemoveBundleFromList = (newBundleList) => {
        setNewBundleGroup(prev => {
            return {
                ...prev,
                children: newBundleList
            }
        })

    }

    console.log("NBG", newBundleGroup)

    return (
        <>
            <Content>
                <Form onSubmit={submitHandler}>
                    <FormGroup {...fieldsetFileUploaderProps_Images}>
                        <FileUploader onChange={imagesChangeHandler} {...fileUploaderProps_Images} />
                    </FormGroup>
                    <TextInput onChange={nameChangeHandler} {...textInputProps_Name} />
                    <Select onChange={categoryChangeHandler} {...selectProps_Category}>{selectItems_Category}</Select>
                    <TextInput onChange={documentationChangeHandler} {...textInputProps_Documentation} />
                    <TextInput onChange={versionChangeHandler} {...textInputProps_Version} />
                    <Select onChange={statusChangeHandler} {...selectProps_Status}>{selectItems_Status}</Select>
                    <TextArea onChange={descriptionChangeHandler} {...textAreaProps_Description} />
                    <AddBundleToBundleGroup onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}/>
                    <Button type="submit">Submit</Button>
                </Form>
            </Content>
        </>
    );
};

export default NewBundleGroup;
