import {useEffect, useState} from "react"
import {Content, Select, SelectItem,} from "carbon-components-react"
import {getAllKCUsers, getAllOrganisations, getAllUsers} from "../../../../integration/Integration"
import i18n from "../../../../i18n"

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

const NewUser = ({onDataChange}) => {


    const [user, setUser] = useState({
        username: "",
        email: "",
        organisation: {}
    })
    const [organisations, setOrganisations] = useState([])
    const [availableUsers, setAvailableUsers] = useState([])

    const changeUser = (field, value, user) => {
        const newObj = {
            ...user,
        }
        newObj[field] = value
        setUser(newObj)
        onDataChange(newObj)
    }

    //TODO BE QUERY REFACTORING
    const getAvailableKcUsers = async () => {
        const kcUsers = (await getAllKCUsers()).kcUsers
        const portalUserUsernames = (await getAllUsers()).userList.map(u=>u.username)

        return kcUsers && kcUsers.filter(kcUser=>!portalUserUsernames.includes(kcUser.username))
    }

    useEffect(() => {
        let isMounted = true;
        (async () => {
            const organisations = (await getAllOrganisations()).organisationList
            const availableUsers = (await getAvailableKcUsers())
            if (isMounted) {
                setOrganisations(organisations)
                setAvailableUsers(availableUsers)
                setUser({
                    username: "nousername",
                    email: "",
                    organisation: organisations[0]
                })
            }
        })()
        return () => { isMounted = false }

    }, [])


    const selectItems_Organisations = organisations.map((organisation) => {
        return (
            <SelectItem
                key={organisation.organisationId}
                value={organisation.organisationId}
                text={organisation.name}
            />
        )
    })
    const selectItems_AvailableUsers = availableUsers && availableUsers.map((user) => {
        return (
            <SelectItem
                key={user.username}
                value={user.username}
                text={user.username}
            />
        )
    })


    const nameChangeHandler = (e) => {
        changeUser("username", e.target.value, user)
    }

    const organisationChangeHandler = (e) => {
        const selectedOrganisation = organisations.filter(o => o.organisationId === e.target.value)[0]
        changeUser("organisation", selectedOrganisation, user)
    }

    return (
        <>
            <Content>
                <Select value={user.name} onChange={nameChangeHandler}
                        id={"name"}
                        labelText={i18n.t('component.bundleModalFields.name')}>
                    <SelectItem
                        key="nousername"
                        value="nousername"
                        text={i18n.t('component.button.selectOneUsername')}
                    />
                    {selectItems_AvailableUsers}
                </Select>
                <Select value={user.organisation.organisationId} onChange={organisationChangeHandler}
                        id={"organisation"}
                        labelText={i18n.t('component.bundleModalFields.organisation')}>{selectItems_Organisations}</Select>
            </Content>
        </>
    )

}
export default NewUser
