import {useEffect, useState} from "react";


import {
    Button,
    DataTable,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableHeader,
    TableRow, TableToolbar, TableToolbarContent,
} from 'carbon-components-react';
import UserManagementOverflowMenu from "./overflow-menu/UserManagementOverflowMenu";
import {ModalAddNewUser} from "./modal-add-new-user/ModalAddNewUser";
import {getAllUsers, getSingleOrganisation} from "../../integration/Integration";

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


BUNDLE
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
}
 */

const rows = [
    {
        id: "my",
        username: 'my',
        email: 'email',
        organisation: 'org',
    },
];

const headers = [
    {
        key: 'username',
        header: 'Username',
    },
    {
        key: 'email',
        header: 'Email',
    },
    {
        key: 'organisation',
        header: 'Organisation',
    },
    {
        key: 'overflow',
        header: 'overflow',
    }
];

/*
{
    "id": "string",
    "created": "2021-09-22T13:37:39.364Z",
    "username": "string",
    "enabled": true,
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "organisationIds": [
      "string"
    ]
  }
*/


const UserManagementPage = () => {
    const [users, setUsers] = useState([]);

    // fetches the users to show
    useEffect(() => {
        const init = async () => {
            //users already inserted in the portalUsers
            const userList = (await getAllUsers()).userList

            //for every user get the organisations name
            const userListWithOrganisation = await Promise.all(userList.map((async (user) => {
                if (user.organisationIds) {
                    //get the current organisation name
                    const organisations = await Promise.all(user.organisationIds.map((async (oid) => {
                            const organisation = (await getSingleOrganisation(oid)).organisation
                            console.log(organisation)
                            return organisation
                        }
                    )))

                    console.log("organisations", organisations)

                    return {
                        ...user,
                        organisation: organisations[0].name
                    }
                }

                return {
                    ...user,
                    organisation: null
                }
            })))

            console.log(userListWithOrganisation)
            setUsers(userListWithOrganisation)
        }

        init()
    }, []);

    return (
        <DataTable rows={users} headers={headers}>
            {({rows, headers, getTableProps, getHeaderProps, getRowProps}) => (
                <TableContainer title="Users Management">
                    <TableToolbar>
                        <TableToolbarContent>
                            <ModalAddNewUser/>
                        </TableToolbarContent>
                    </TableToolbar>
                    <Table {...getTableProps()}>
                        <TableHead>
                            <TableRow>
                                {headers.map((header) => (
                                    <TableHeader {...getHeaderProps({header})}>
                                        {header.header}
                                    </TableHeader>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.map(row => (
                                <TableRow {...getRowProps({row})}>
                                    {row.cells.map((cell) => {
                                            if (cell.id !== row.id + ":overflow") return <TableCell
                                                key={cell.id}>{cell.value}</TableCell>

                                            return <TableCell key={cell.id}><UserManagementOverflowMenu/></TableCell>
                                        }
                                    )}
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </DataTable>
    )
};


export default UserManagementPage;
