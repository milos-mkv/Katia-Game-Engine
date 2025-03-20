local Behaviour = classes.class()

function Behaviour:init(params)
    print("Behaviour:init")
    self.params = params
    self.gameObject = params.gameObject
    self.scene = params.scene
end


function Behaviour:update(dt)

end

return Behaviour