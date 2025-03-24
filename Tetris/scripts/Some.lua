---------------------------------------------------------------------------------------------------------------------------------------
-- @file       GameLogic.lua
-- @author     Milos Milicevic (milosh.mkv@gmail.com)
--
-- @version     0.1
-- @date        2025-03-21
-- @copyright 	Copyright (c) 2025
--
-- Distributed under the MIT software license, see the accompanying file LICENCE or http://www.opensource.org/licenses/mit-license.php.
---------------------------------------------------------------------------------------------------------------------------------------

local Behaviour = require("Behaviour")
local ASD = classes.class(Behaviour)
require("Constants")



function ASD:init(params)
	Behaviour.init(self, params)
	print("GameLogic:init")
	for key, value in pairs(params) do
		print(key, value)
	end

end



aa =0
function ASD:update(dt)
	aa = aa + dt
	if aa > 0.5 then
		aa = 0
        print(dt)
	end


	 --if (Input:isKeyJustPressed(KEY_R)) then
	 --print("WTF")
	--	SceneManager:setActiveScene("MainScene")
	-- 	AudioManager:play(self.params.music)
--	 end

end

return ASD
























































