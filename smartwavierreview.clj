(config
       (password-field
        :name "apiKey"
        :label "sw-api-key"
        :placeholder "enter the api key generated with smart wavier"
        :required true)
       )
      
(default-source (http/get :base-url "https://api.smartwaiver.com/v4" 
                          (header-params "Accept" "application/json"))
                (paging/no-pagination)
                (auth/apikey-custom-header :headerName "sw-api-key")
                (error-handler
                 (when :status 404 :message "not found" :action fail)
                 (when :status 401 :action fail)
                 )
                
)

(entity WAVIER
        (api-docs-url "https://api.smartwaiver.com/docs/v4/#api-Waivers-WaiverList")
        (source (http/get :url "/waivers")
                (extract-path "waivers")
                (qery-params
                 "limit" 300))

        (fields
         id :id :<= "waiverId"
         template_id:<= "templateId"
         title
         created_on:<= "createdOn"
         expiration_date:<= "expirationDate"
         expired
         verified
         kiosk
         first_name:<= "firstName"
         middle_name:<= "middleName"
         last_Name:<= "lastName"
         dob
         is_minor:<= "isMinor"
         auto_tag:<= "autoTag")
        (relate
         (contains-list-of FLAG :inside-prop "flags")
         (contains-list-of TAG :inside-prop "tags" :as tag))
        (sync-plan
         (change-capture-cursor
          (subset/by-time (query-params "fromDts" "$FROM"
                                        "toDts" "$TO")
                          (format "yyyy-MM-dd'T'HH:mm:ssz")
                          (initial  "2021-12-23")
                          )))
        (setup-test 
         (upon-receving :code 200 ( pass) ))
        )

(entity FLAG 
        (api-docs-url "https://api.smartwaiver.com/docs/v4/#api-Waivers-WaiverList")
        (source (http/get :url "/waivers")
                (extract-path "waivers.flags")
                (qery-params
                 "limit" 300))
        (fields
        displayText
        reason )
        
        (Save)
        (relate (needs WAVIER prop: "id" ))
        
)






