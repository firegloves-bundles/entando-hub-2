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


const UserManagementPage = () => {
    const [users, setUsers] = useState([]);

    // fetches the users to show
    useEffect(() => {
        const init = async () => {
        };

        init();
    }, []);

    return (
        <DataTable rows={rows} headers={headers}>
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
                            {rows.map((row, index) => (
                                <TableRow {...getRowProps({row})}>
                                    {row.cells.map((cell) => {
                                            if (cell.id !== row.id + ":overflow") return <TableCell
                                                key={cell.id}>{cell.id + "-" + cell.value}</TableCell>

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
