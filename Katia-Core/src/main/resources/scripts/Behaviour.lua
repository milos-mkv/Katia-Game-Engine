local Behaviour = classes.class()

function Behaviour:init(params)
    print("Behaviour:init")
    self.gameObject = params.gameObject
    self.scene = params.scene
end


function Behaviour:update(dt)

end

return Behaviour