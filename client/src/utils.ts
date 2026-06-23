import { S3Client } from "@aws-sdk/client-s3";

export interface StringArray {
    [index: string] : string
}

export const s3Client = new S3Client({
    region: 'us-east-1',
    credentials: {
        accessKeyId: import.meta.env.NG_APP_AWS_ACCESS_ID,
        secretAccessKey: import.meta.env.NG_APP_AWS_SECRET_ACCESS_KEY
    },
    requestChecksumCalculation: "WHEN_REQUIRED"
});

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


