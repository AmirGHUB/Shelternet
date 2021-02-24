TO RUN AUTOMATED TESTS

Open Postman

Create New Enviornment

Create "url" enviornment variable, and set it to the root enviornment you wish to test against (see url below for more)
https://learning.postman.com/docs/sending-requests/managing-environments/

Create "password" enviornment variable, and set it to the password for that enviornment (staging, local, prod, etc..)

Import "shelternet-smoke-test.postman_collection" collection from root of project folder

Click Collections and mouse over "shelternet staging" and click the arrow pointing to the right (see url below for more)
https://learning.postman.com/docs/running-collections/intro-to-collection-runs/

Click Run

Click Run Shelternet Smoke Test