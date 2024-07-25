# Other Abilities

Other small functions:

## Parse Swagger Api Doc

This function will automatically parse and generate request messages for all interfaces, send them to the `Organizer`, and allow for testing parameter modifications.

> If you want to automatically send the generated requests to the server, you can check the option in the Galaxy -> Settings window.

**Instructions for Use**:

Right-click on any response message in the Editor and select "Parse Swagger Doc".

## Bypass Host Check

Bypass server-side validation on CSRF/SSRF testing points for the host.

> If you want to customize bypassing the template, you can modify the `${Work Dir}/templates/bypassHostCheckTemplate.txt` file.
>
> [Work Dir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Work-Dir), [Template](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Tempalte)

**Instructions for Use**:

Enter the URL to be attacked at the test point, right-click on it, select `Send To Intruder`, then in `Intruder` select `Payload type -> Extension-generated

## Bypass Auth Of Path

Bypass certain authentication/authorization/interception by modifying the path.

**Instructions for Use**:

Start by selecting the path that needs to be bypassed with `/`, then right-click and select `Send To Intruder`. In the `Intruder` tab, choose `Payload type -> Extension-generated`, `Selected generator -> Bypass Auth Of Path`, and then click `Start attack`.
