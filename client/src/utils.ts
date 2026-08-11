export interface StringArray {
    [index: string] : string
}

export function getUserIdNumber(userId : String) {
    // Auth0 User ID : {connection}|{userIdNumber}. 
    // Connection is irrelevant and the vertical bar character isn't recommended for S3 naming convention
    return userId.split("|")[1];
}

export const sortFields : StringArray = {
    'Date created': 'createdAt',
    'Last viewed': 'viewedAt'
}; 

export const orderDirection : StringArray = {
    'A-Z': 'asc',
    'Z-A': 'desc',
    'Newest first': 'desc',
    'Oldest first': 'asc'
};


