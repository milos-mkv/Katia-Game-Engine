local Behaviour = require("Behaviour")
local PlayerBehaviour = classes.class(Behaviour)

function PlayerBehaviour:init(params)
    Behaviour.init(self, params)
    print("PlayerBehaviour:init - " .. tostring(self))
    self.scene = params.scene

    self.gameObject:getComponent("Transform"):setX(10)
    print(self.gameObject:getComponent("Transform"):getX())

end

function PlayerBehaviour:update(dt)
    Behaviour.update(self, dt)
    print("PlayerBehaviour:update")
end

return PlayerBehaviour
