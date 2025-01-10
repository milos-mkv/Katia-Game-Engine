local Behaviour = classes.class()
require("Constants")
function Behaviour:init(params)
    print("Behaviour:init")
    self.gameObject = params.gameObject
    for k, v in pairs(params) do
        print(k,v)
    end
    self.pos = self.gameObject:getComponent("Transform"):getPosition()
    self.rot = self.gameObject:getComponent("Transform"):getRotation()
end

function Behaviour:update(dt)
    -- Update
    self.rot = self.rot +0.01
    self.gameObject:getComponent("Transform"):setRotation(self.rot)
    if Input:isKeyDown(KEY_A) then
        self.pos.x = self.pos.x - 1
    end
    if Input:isKeyDown(KEY_D) then
        self.pos.x = self.pos.x + 1
    end
    if Input:isKeyDown(KEY_W) then
        self.pos.y = self.pos.y + 1
    end
    if Input:isKeyDown(KEY_S) then
        self.pos.y = self.pos.y - 1
    end
end

return Behaviour