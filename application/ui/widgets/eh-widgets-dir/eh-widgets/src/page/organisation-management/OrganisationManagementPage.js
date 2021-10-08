import React, { useEffect, useState } from "react"

import {
  Content,
  DataTable,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableHeader,
  TableRow,
  TableToolbar,
  TableToolbarContent,
} from "carbon-components-react"
import OrganisationManagementOverflowMenu from "./overflow-menu/OrganisationManagementOverflowMenu"
import { ModalAddNewOrganisation } from "./modal-add-new-user/ModalAddNewOrganisation"
import {
  getAllUsers,
  getSingleOrganisation,
} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb"
import "./organisation-managment-page.scss"

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

const headers = [
  {
    key: "username",
    header: "Username",
  },
  {
    key: "email",
    header: "Email",
  },
  {
    key: "organisation",
    header: "Organisation",
  },
  {
    key: "overflow",
    header: "",
  },
]

const OrganisationManagementPage = () => {
  const [reloadToken, setReloadToken] = useState(
    new Date().getTime().toString()
  )
  const [users, setUsers] = useState([])

  // fetches the users to show
  useEffect(() => {
    //TODO BE QUERY REFACTORING
    const getAllPortalUsers = async () => {
      const userList = (await getAllUsers()).userList
      //for every user get the organisations name
      const userListWithOrganisation = await Promise.all(
        userList.map(async (user) => {
          if (user.organisationIds) {
            //get the current organisation name
            const organisations = await Promise.all(
              user.organisationIds.map(async (oid) => {
                const organisation = (await getSingleOrganisation(oid))
                  .organisation
                return organisation
              })
            )

            return {
              ...user,
              email: user.email ? user.email : "",
              organisation: organisations[0],
            }
          }
          return {
            ...user,
            email: user.email ? user.email : "",
            organisation: null,
          }
        })
      )

      return userListWithOrganisation
    }
    ;(async () => {
      setUsers(await getAllPortalUsers())
    })()
  }, [reloadToken])

  const onAfterSubmit = () => {
    setReloadToken(new Date().getTime().toString())
  }
  return (
    <>
      <Content className="UserManagementPage">
        <div className="UserManagementPage-wrapper">
          <div className="bx--row">
            <div className="bx--col-lg-16 UserManagementPage-breadcrumb">
              <EhBreadcrumb />
            </div>
          </div>
          <div className="bx--row">
            <div className="bx--col-lg-16 UserManagementPage-section">
              <DataTable rows={users} headers={headers}>
                {({
                  rows,
                  headers,
                  getTableProps,
                  getHeaderProps,
                  getRowProps,
                }) => (
                  <TableContainer title="Organisations Management">
                    <TableToolbar>
                      <TableToolbarContent>
                        <ModalAddNewOrganisation onAfterSubmit={onAfterSubmit} />
                      </TableToolbarContent>
                    </TableToolbar>
                    <Table {...getTableProps()}>
                      <TableHead>
                        <TableRow>
                          {headers.map((header) => (
                            <TableHeader {...getHeaderProps({ header })}>
                              {header.header}
                            </TableHeader>
                          ))}
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {rows.map((row) => (
                          <TableRow {...getRowProps({ row })}>
                            {row.cells.map((cell, index) => {
                              if (cell.id !== row.id + ":overflow") {
                                return (
                                  <TableCell key={cell.id}>
                                    {index === 2
                                      ? cell.value
                                        ? cell.value.name
                                        : "---"
                                      : cell.value}
                                  </TableCell>
                                )
                              }
                              return (
                                <TableCell key={cell.id}>
                                  <OrganisationManagementOverflowMenu
                                    userObj={{
                                      username: row.cells[0].value,
                                      email: row.cells[1].value,
                                      organisation: row.cells[2].value,
                                    }}
                                    onAfterSubmit={onAfterSubmit}
                                  />
                                </TableCell>
                              )
                            })}
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                )}
              </DataTable>
            </div>
          </div>
        </div>
      </Content>
    </>
  )
}

export default OrganisationManagementPage
